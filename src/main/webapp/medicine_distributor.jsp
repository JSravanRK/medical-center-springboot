<%--
    medicine_distributor.jsp
    Updated for Spring Boot: uses List<Map<String,Object>> instead of ResultSet
--%>
<%
    try {
        if(Integer.parseInt(session.getAttribute("desig_id").toString()) != 9) {
%>
    <jsp:forward page="InvalidPage.jsp"></jsp:forward>
<%  }
    } catch(Exception e) {
%>
    <jsp:forward page="InvalidPage.jsp"></jsp:forward>
<%} %>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="include/header.jsp" %>
<%@include file="include/distributor_menu.jsp" %>
<%@include file="include/alwaysinclude.jsp" %>

<div id="page_title">Medicine Distributor</div>

<div id="page_body">
<br><br>
    <form method="post">
        <table class="stock_table" id="stock_table" width="100%">
        <thead>
            <th>Sr. No.</th>
            <th>Medicine Type</th>
            <th>Generic Name</th>
            <th>Commercial Name</th>
            <th>Quantity</th>
            <th>Location</th>
        </thead>
        <tbody>
            <%
            int presNo = 0;
            java.util.List<java.util.Map<String,Object>> medicines =
                (java.util.List<java.util.Map<String,Object>>) request.getAttribute("data");
            Object presObj = request.getAttribute("prescription_id");
            if (presObj != null) presNo = ((Number)presObj).intValue();
            int i = 0;
            if (medicines != null) {
                for (java.util.Map<String,Object> row : medicines) {
                    i++;
            %>
            <tr>
                <td><%= i %></td>
                <td><%= row.get("med_type") %></td>
                <td><%= row.get("med_gen_name") %></td>
                <td><%= row.get("med_com_name") + " (" + row.get("med_weight") + "mg)" %></td>
                <td><%= row.get("med_qty") %></td>
                <td><a href="#">see loc</a></td>
            </tr>
            <%  }
            } %>
            <tr>
                <td colspan="3" align="center">
                    <input type="button" name="med_dist_cancel" id="med_dist_cancel" value="Cancel" />
                </td>
                <td colspan="3" align="center">
                    <input type="button" name="med_dist_submit" id="med_dist_submit" value="Delivered" />
                </td>
            </tr>
        </tbody>
        </table>
    </form>
    <br><br><br>
</div>
<%@include file="include/footer.jsp" %>

<script type="text/javascript">
$(document).ready(function(){
    $('#med_dist_submit').click(function(){
        $.post("MedicineDelivered",
            {pres_id: <%= presNo %>},
            function(){
                window.location = "med_dist_first.jsp";
            }
        );
    });
    $('#med_dist_cancel').click(function(){
        window.location = "med_dist_first.jsp";
    });
});
</script>
