package com.medicalcenter.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DoctorController
 *
 * Replaces these original servlets:
 *  - GetStdInfo.java            → /GetStdInfo
 *  - GetMedInfo.java            → /GetMedInfo
 *  - SubmitStdPrescription.java → /SubmitStdPrescription
 *  - GetGeneralAdvice.java      → /GetGeneralAdvice
 *  - GetOEValue.java            → /GetOEValue
 */
@Controller
public class DoctorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================
    // GET STUDENT INFO
    // Replaces: servlet/GetStdInfo.java
    // =========================================================
    @RequestMapping(value = "/GetStdInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String getStudentInfo(HttpServletRequest request) {

        String regId = request.getParameter("reg_id");

        String sql = "SELECT full_name, sex, " +
                     "(YEAR(CURDATE())-YEAR(date_of_birth)) - (RIGHT(CURDATE(),5)<RIGHT(date_of_birth,5)) AS age, " +
                     "present_address, photo, dept_code " +
                     "FROM student_personal_info, student, department, degree_offered_by_dept, student_batch " +
                     "WHERE student_personal_pk_fk = student.student_pk " +
                     "AND department.dept_pk = degree_offered_by_dept.department_fk " +
                     "AND degree_offered_by_dept.dept_degree_pk = student_batch.dept_degree_fk " +
                     "AND student_batch.student_batch_pk = student.student_batch_fk " +
                     "AND student.registration_no = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, regId);

        if (!rows.isEmpty()) {
            Map<String, Object> row = rows.get(0);
            request.setAttribute("name",    row.get("full_name"));
            request.setAttribute("sex",     row.get("sex"));
            request.setAttribute("age",     row.get("age"));
            request.setAttribute("address", row.get("present_address"));
            request.setAttribute("dept",    row.get("dept_code"));
            return "forward:/add_std_prescription.jsp";
        } else {
            return "forward:/InvalidPatient.jsp";
        }
    }

    // =========================================================
    // GET MEDICINE INFO (returns JSON)
    // Replaces: servlet/GetMedInfo.java
    // =========================================================
    @RequestMapping(value = "/GetMedInfo", method = {RequestMethod.GET, RequestMethod.POST},
                    produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getMedInfo(HttpServletRequest request) {

        String medType = request.getParameter("medType");

        String sql = "SELECT med_com_name_pk, med_com_name, med_weight " +
                     "FROM medicine_com_info, medicine_gen_info " +
                     "WHERE med_gen_name_fk = med_gen_name_pk AND med_type = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, medType);

        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> row : rows) {
            int id         = ((Number) row.get("med_com_name_pk")).intValue();
            String comName = (String) row.get("med_com_name");
            Object weightObj = row.get("med_weight");
            int weight = 0;
            if (weightObj != null) {
                try { weight = Integer.parseInt(weightObj.toString()); } catch (Exception ignored) {}
            }

            String name = (weight == 0) ? comName : comName + "  (" + weight + " mg)";

            JSONObject obj = new JSONObject();
            obj.put("id",   id);
            obj.put("name", name);
            jsonArray.add(obj);
        }

        return JSONValue.toJSONString(jsonArray);
    }

    // =========================================================
    // GET GENERAL ADVICE (returns JSON)
    // Replaces: servlet/GetGeneralAdvice.java
    // =========================================================
    @RequestMapping(value = "/GetGeneralAdvice", method = {RequestMethod.GET, RequestMethod.POST},
                    produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getGeneralAdvice(HttpServletRequest request) {

        String advId = request.getParameter("gadv_id");

        String sql = "SELECT general_adv_txt FROM general_advice WHERE general_adv_pk = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, advId);

        String advText = rows.isEmpty() ? null : (String) rows.get(0).get("general_adv_txt");
        return JSONValue.toJSONString(advText);
    }

    // =========================================================
    // GET ON-EXAMINATION VALUE (returns JSON)
    // Replaces: servlet/GetOEValue.java
    // =========================================================
    @RequestMapping(value = "/GetOEValue", method = {RequestMethod.GET, RequestMethod.POST},
                    produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getOEValue(HttpServletRequest request) {

        String oeName = request.getParameter("oename");

        String sql = "SELECT observation_value FROM diagnosis_info " +
                     "WHERE observation_name = ? AND observation_type = 'on_examination'";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, oeName);

        String value = rows.isEmpty() ? null : (String) rows.get(0).get("observation_value");
        return JSONValue.toJSONString(value);
    }

    // =========================================================
    // SUBMIT STUDENT PRESCRIPTION
    // Replaces: servlet/SubmitStdPrescription.java
    // =========================================================
    @RequestMapping(value = "/SubmitStdPrescription", method = {RequestMethod.GET, RequestMethod.POST})
    public String submitPrescription(HttpServletRequest request, HttpSession session) {

        try {
            String empFk          = session.getAttribute("user_pk").toString();
            String reg            = request.getParameter("regno");
            String diagnosisDetail = request.getParameter("diagnosis_detail");
            String reDate         = request.getParameter("re_dt");
            String genAdv         = request.getParameter("gen_adv");
            String medStr         = request.getParameter("medicineinfo");

            JSONArray medicineArray = (JSONArray) JSONValue.parse(medStr);

            // Get student PK from registration number
            String stdQuery = "SELECT student_pk FROM student WHERE registration_no = ?";
            List<Map<String, Object>> stdRows = jdbcTemplate.queryForList(stdQuery, reg);

            if (stdRows.isEmpty()) {
                return "forward:/prescription_error.jsp";
            }
            String stdFk = stdRows.get(0).get("student_pk").toString();

            // Today's date
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Insert prescription header
            String insertPrescription =
                "INSERT INTO std_prescription_info " +
                "(student_pk_fk, emplaoyee_pk_fk, prescription_dt, diagnosis_detail, reconsult_dt, general_advice) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertPrescription,
                Long.parseLong(stdFk),
                Integer.parseInt(empFk),
                java.sql.Date.valueOf(today),
                diagnosisDetail,
                reDate,
                genAdv);

            // Get the last inserted prescription ID
            Long prescriptionFk = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()", Long.class);

            // Insert each medicine row
            String insertMed =
                "INSERT INTO patient_med_info " +
                "(med_com_name_fk, no_of_doses, medication_inst_fk, day_duration, med_qty, std_prescription_fk, state) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

            for (Object item : medicineArray) {
                JSONObject medObj    = (JSONObject) item;
                String medId         = (String) medObj.get("med_id");
                String dose          = (String) medObj.get("med_dose");
                String tempdr        = (String) medObj.get("med_dr");
                String tempqty       = (String) medObj.get("med_qty");
                String tempInstId    = (String) medObj.get("med_inst");

                Integer instId = null;
                if (tempInstId != null && !tempInstId.isEmpty() && !tempInstId.equals("0")) {
                    instId = Integer.parseInt(tempInstId);
                }

                jdbcTemplate.update(insertMed,
                    Integer.parseInt(medId),
                    dose,
                    instId,
                    Integer.parseInt(tempdr),
                    Integer.parseInt(tempqty),
                    prescriptionFk,
                    "notavailable");
            }

            return "forward:/doctor_first_if.jsp";

        } catch (Exception e) {
            System.out.println("SubmitStdPrescription Error: " + e);
            return "forward:/prescription_error.jsp";
        }
    }
}
