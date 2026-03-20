package com.medicalcenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * AdminController
 *
 * Replaces these original servlets:
 *  - AddNewMed.java  → /AddNewMed
 *  - AddNewcc.java   → /AddNewcc
 *  - AddNewInv.java  → /AddNewInv
 */
@Controller
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================
    // ADD NEW MEDICINE (Generic + Commercial Info)
    // Replaces: servlet/AddNewMed.java
    // =========================================================
    @RequestMapping(value = "/AddNewMed", method = {RequestMethod.GET, RequestMethod.POST})
    public String addNewMed(HttpServletRequest request) {

        String type    = request.getParameter("mtype");
        String weight  = request.getParameter("mweight");
        String gName   = request.getParameter("genname");
        String cName   = request.getParameter("comname");

        try {
            // Step 1: Insert into medicine_gen_info and get its PK
            jdbcTemplate.update(
                "INSERT INTO medicine_gen_info (med_gen_name, med_type) VALUES (?, ?)",
                gName, type);

            Long genNameFk = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Step 2: Insert into medicine_com_info
            jdbcTemplate.update(
                "INSERT INTO medicine_com_info (med_gen_name_fk, med_com_name, med_weight) VALUES (?, ?, ?)",
                genNameFk, cName, weight);

        } catch (Exception e) {
            System.out.println("AddNewMed Error: " + e);
        }

        // Original servlet wrote nothing to response body - just close output
        // Forward back to add medicine page
        return "forward:/addNewMed.jsp";
    }

    // =========================================================
    // ADD NEW CHIEF COMPLAINT
    // Replaces: servlet/AddNewcc.java
    // =========================================================
    @RequestMapping(value = "/AddNewcc", method = {RequestMethod.GET, RequestMethod.POST})
    public String addNewChiefComplaint(HttpServletRequest request) {

        String ccName = request.getParameter("cc_name");

        try {
            jdbcTemplate.update(
                "INSERT INTO diagnosis_info (observation_name, observation_type) VALUES (?, 'cheif_complaint')",
                ccName);
        } catch (Exception e) {
            System.out.println("AddNewcc Error: " + e);
        }

        return "forward:/addnewcc.jsp";
    }

    // =========================================================
    // ADD NEW INVESTIGATION
    // Replaces: servlet/AddNewInv.java
    // =========================================================
    @RequestMapping(value = "/AddNewInv", method = {RequestMethod.GET, RequestMethod.POST})
    public String addNewInvestigation(HttpServletRequest request) {

        String invName = request.getParameter("inv_name");

        try {
            jdbcTemplate.update(
                "INSERT INTO diagnosis_info (observation_name, observation_type) VALUES (?, 'investigation')",
                invName);
        } catch (Exception e) {
            System.out.println("AddNewInv Error: " + e);
        }

        return "forward:/addnewinv.jsp";
    }
}
