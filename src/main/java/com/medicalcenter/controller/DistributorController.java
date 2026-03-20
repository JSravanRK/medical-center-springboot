package com.medicalcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * DistributorController
 *
 * Replaces these original servlets:
 *  - GetUndeliveredMed.java  → /GetUndeliveredMed
 *  - MedicineDelivered.java  → /MedicineDelivered
 */
@Controller
public class DistributorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================
    // GET UNDELIVERED MEDICINES FOR A PATIENT
    // Replaces: servlet/GetUndeliveredMed.java
    // =========================================================
    @RequestMapping(value = "/GetUndeliveredMed", method = {RequestMethod.GET, RequestMethod.POST})
    public String getUndeliveredMed(HttpServletRequest request) {

        String regNo = request.getParameter("reg_id");

        // Get the latest prescription for this student
        String query1 = "SELECT std_prescription_pk FROM std_prescription_info, student " +
                        "WHERE student_pk_fk = student_pk " +
                        "AND registration_no = ? " +
                        "ORDER BY std_prescription_pk DESC LIMIT 1";

        List<Map<String, Object>> prescRows = jdbcTemplate.queryForList(query1, regNo);

        if (prescRows.isEmpty()) {
            return "forward:/med_dist_error.jsp";
        }

        int prescriptionId = ((Number) prescRows.get(0).get("std_prescription_pk")).intValue();

        // Call stored procedure to update medicine state
        jdbcTemplate.update("CALL update_med_state(?)", prescriptionId);

        // Get the undelivered medicines
        String query2 = "SELECT med_type, med_gen_name, med_com_name_pk, med_com_name, med_weight, med_qty " +
                        "FROM medicine_gen_info, medicine_com_info, patient_med_info " +
                        "WHERE med_com_name_fk = med_com_name_pk " +
                        "AND std_prescription_fk = ? " +
                        "AND state = 'undelivered' " +
                        "AND med_gen_name_fk = med_gen_name_pk";

        List<Map<String, Object>> medicines = jdbcTemplate.queryForList(query2, prescriptionId);

        if (medicines.isEmpty()) {
            return "forward:/med_dist_error.jsp";
        }

        request.setAttribute("data", medicines);
        request.setAttribute("prescription_id", prescriptionId);
        return "forward:/medicine_distributor.jsp";
    }

    // =========================================================
    // MARK MEDICINES AS DELIVERED
    // Replaces: servlet/MedicineDelivered.java
    // =========================================================
    @RequestMapping(value = "/MedicineDelivered", method = {RequestMethod.GET, RequestMethod.POST})
    public String medicineDelivered(HttpServletRequest request) {

        String presIdStr = request.getParameter("pres_id");
        int presNo = Integer.parseInt(presIdStr.trim());

        // Update state from 'undelivered' to 'delivered'
        String updateSql = "UPDATE patient_med_info SET state = 'delivered' " +
                           "WHERE std_prescription_fk = ? AND state = 'undelivered'";
        jdbcTemplate.update(updateSql, presNo);

        // Call stored procedure to log the distribution
        jdbcTemplate.update("CALL insert_dist_log(?)", presNo);

        // Returns empty – the original servlet wrote nothing to response
        // The JSP page will handle the redirect/display
        return "forward:/med_dist_first.jsp";
    }
}
