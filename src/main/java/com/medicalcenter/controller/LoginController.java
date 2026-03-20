package com.medicalcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * LoginController
 *
 * Replaces: servlet/LoginVerify.java
 * URL Mapping: /LoginVerify  (same as original)
 *
 * Logic:
 *  - If username starts with a digit  → it's a STUDENT login
 *  - Otherwise                        → it's an EMPLOYEE login
 *  - After successful login, sets session attributes and forwards to the
 *    correct JSP based on the employee's designation_id:
 *      9  → Medicine Distributor → med_dist_first.jsp
 *      10 → Doctor               → doctor_first_if.jsp
 *      11 → Pharmacist           → user_profile.jsp
 *      else → Employee           → view_emp_prescription.jsp
 */
@Controller
public class LoginController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Handles both GET and POST (same as original processRequest)
    @RequestMapping(value = "/LoginVerify", method = {RequestMethod.GET, RequestMethod.POST})
    public String login(HttpServletRequest request, HttpSession session) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty()) {
            return "forward:/login.jsp";
        }

        char firstChar = username.charAt(0);

        // ---- STUDENT LOGIN (username starts with a digit) ----
        if (firstChar >= '0' && firstChar <= '9') {
            String query = "SELECT student_pk FROM student WHERE registration_no=? AND password=?";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, username, password);

            if (!rows.isEmpty()) {
                int userPk = ((Number) rows.get(0).get("student_pk")).intValue();
                session.setAttribute("user_pk", userPk);
                session.setAttribute("user_type", "student");
                return "forward:/student_profile.jsp";
            } else {
                return "forward:/login.jsp";
            }
        }

        // ---- EMPLOYEE LOGIN (username does NOT start with a digit) ----
        String query = "SELECT employee_pk, designation_fk FROM employee, employee_status " +
                       "WHERE employee.login_id=? AND employee.password=? " +
                       "AND employee_pk = employee_status_pk_fk";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, username, password);

        if (!rows.isEmpty()) {
            int userPk        = ((Number) rows.get(0).get("employee_pk")).intValue();
            int designationId = ((Number) rows.get(0).get("designation_fk")).intValue();

            session.setAttribute("user_pk",   userPk);
            session.setAttribute("desig_id",  designationId);
            session.setAttribute("user_type", "employee");

            if (designationId == 9) {
                // Medicine Distributor
                return "forward:/med_dist_first.jsp";
            } else if (designationId == 10) {
                // Doctor
                return "forward:/doctor_first_if.jsp";
            } else if (designationId == 11) {
                // Pharmacist
                return "forward:/user_profile.jsp";
            } else {
                // Regular Employee
                return "forward:/view_emp_prescription.jsp";
            }
        }

        // Login failed
        return "forward:/login.jsp";
    }
}
