package com.medicalcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * ImageController
 *
 * Replaces: servlet/RetrieveImage.java
 * URL Mapping: /RetrieveImage
 *
 * Retrieves a student's photo (stored as BLOB in database)
 * and streams it back as image/jpeg response.
 */
@Controller
public class ImageController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/RetrieveImage", method = {RequestMethod.GET, RequestMethod.POST})
    public void retrieveImage(HttpServletRequest request, HttpServletResponse response) {

        String regNo = request.getParameter("regNo");

        String sql = "SELECT photo FROM student_personal_info, student " +
                     "WHERE student.student_pk = student_personal_info.student_personal_pk_fk " +
                     "AND student.registration_no = ?";

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, regNo);

            if (!rows.isEmpty()) {
                Object photoObj = rows.get(0).get("photo");
                if (photoObj instanceof byte[]) {
                    byte[] imageData = (byte[]) photoObj;
                    response.reset();
                    response.setContentType("image/jpeg");
                    response.setContentLength(imageData.length);
                    OutputStream out = response.getOutputStream();
                    out.write(imageData);
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("RetrieveImage Error: " + e);
        }
    }
}
