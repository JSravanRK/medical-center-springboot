package com.medicalcenter.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;

/**
 * PharmacistController
 *
 * Replaces these original servlets:
 *  - StocLedgerSubmit.java → /StocLedgerSubmit
 *  - StocTransfer.java     → /StocTransfer
 */
@Controller
public class PharmacistController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================
    // STOCK LEDGER SUBMIT
    // Replaces: servlet/StocLedgerSubmit.java
    // =========================================================
    @RequestMapping(value = "/StocLedgerSubmit", method = {RequestMethod.GET, RequestMethod.POST})
    public String stockLedgerSubmit(HttpServletRequest request) {

        String companyId   = request.getParameter("company_id");
        String date        = request.getParameter("date");
        String commission  = request.getParameter("commission");
        String empFk       = request.getParameter("emp_fk");
        String medInfoStr  = request.getParameter("slmedinfo");

        JSONArray medicineArray = (JSONArray) JSONValue.parse(medInfoStr);

        try {
            // Insert the company ledger header
            String insertComp = "INSERT INTO stock_ledger_comp (comp_id_fk, employee_pk_fk, purchase_dt, commission) " +
                                "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertComp,
                Integer.parseInt(companyId),
                empFk != null ? Integer.parseInt(empFk) : null,
                Date.valueOf(date),
                Integer.parseInt(commission));

            // Get the last inserted sl_no
            Long slNo = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Insert each medicine entry
            String insertMed = "INSERT INTO stock_ledger_med " +
                               "(sl_no_fk, med_com_name_fk, medicine_qty, pp_price, manufacturing_dt, expiring_dt) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";

            for (Object item : medicineArray) {
                JSONObject medObj = (JSONObject) item;
                String medId  = (String) medObj.get("med_id");
                String qty    = (String) medObj.get("med_qty");
                String pp     = (String) medObj.get("med_pp");
                String mfgDt  = (String) medObj.get("med_mfgdt");
                String expDt  = (String) medObj.get("med_expdt");

                jdbcTemplate.update(insertMed,
                    slNo,
                    Integer.parseInt(medId),
                    Integer.parseInt(qty),
                    Double.parseDouble(pp),
                    Date.valueOf(mfgDt),
                    Date.valueOf(expDt));
            }

        } catch (Exception e) {
            System.out.println("StocLedgerSubmit Error: " + e);
        }

        // Original wrote "Stock ledger submit servlet" to output - just forward back
        return "forward:/stock_ledger_entry.jsp";
    }

    // =========================================================
    // STOCK TRANSFER (Central Stock → Sub Stock)
    // Replaces: servlet/StocTransfer.java
    // =========================================================
    @RequestMapping(value = "/StocTransfer", method = {RequestMethod.GET, RequestMethod.POST})
    public String stockTransfer(HttpServletRequest request) {

        String[] medIds        = request.getParameterValues("med_id");
        String[] transferQtys  = request.getParameterValues("transferred_qty");

        if (medIds != null) {
            for (int i = 0; i < medIds.length; i++) {
                if (transferQtys[i] == null || transferQtys[i].isEmpty()) {
                    continue;
                }
                int mId  = Integer.parseInt(medIds[i]);
                int tQty = Integer.parseInt(transferQtys[i]);

                // Call the stored procedure for each medicine
                jdbcTemplate.update("CALL transfer_stock(?, ?)", mId, tQty);
            }
        }

        return "forward:/transfer_successful.jsp";
    }
}
