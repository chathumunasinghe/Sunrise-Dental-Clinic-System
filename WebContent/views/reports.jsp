<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reports - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Clinic Reports</div>
        <div class="page-subtitle">Daily appointments and revenue insights &middot; <span class="role-badge solid-admin">Admin only</span></div>

        <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><%= request.getAttribute("error") %></div><% } %>

        <div class="card">
            <h3 style="margin-top:0;">Daily Appointments</h3>
            <form action="${pageContext.request.contextPath}/reports" method="get" style="display:flex; gap:12px; align-items:flex-end; margin-bottom: 18px;">
                <div class="field" style="flex:1; margin-bottom:0;">
                    <label>Date</label>
                    <input type="date" class="form-control" name="date" value="<%= request.getAttribute("selectedDate") == null ? "" : request.getAttribute("selectedDate") %>" required />
                </div>
                <button type="submit" class="btn" style="width:auto; padding:11px 24px;">View</button>
            </form>

            <%
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> daily = (List<Map<String, Object>>) request.getAttribute("dailyAppointments");
                if (daily != null) {
            %>
                <table class="data-table">
                    <tr>
                        <th>Appointment #</th><th>Patient</th><th>Dentist</th>
                        <th>Treatment</th><th>Fee</th><th>Status</th>
                    </tr>
                    <% for (Map<String, Object> row : daily) { %>
                        <tr>
                            <td><%= row.get("appointment_number") %></td>
                            <td><%= row.get("patient_name") %></td>
                            <td><%= row.get("dentist_name") %></td>
                            <td><%= row.get("treatment_name") %></td>
                            <td><%= row.get("consultation_fee") %></td>
                            <td><%= row.get("status") %></td>
                        </tr>
                    <% } %>
                </table>
                <% if (daily.isEmpty()) { %><p style="color: var(--text-muted);">No appointments on this date.</p><% } %>
            <% } %>
        </div>

        <div class="card">
            <h3 style="margin-top:0;">Revenue by Treatment Type</h3>
            <table class="data-table">
                <tr><th>Treatment</th><th>Total Revenue (LKR)</th></tr>
                <%
                    @SuppressWarnings("unchecked")
                    Map<String, Double> revenue = (Map<String, Double>) request.getAttribute("revenueByTreatment");
                    if (revenue != null) {
                        for (String key : revenue.keySet()) {
                %>
                        <tr><td><%= key %></td><td><%= revenue.get(key) %></td></tr>
                <%
                        }
                    }
                %>
            </table>
            <p style="margin-top:16px; font-size:15px;"><strong>Total Revenue: LKR <%= request.getAttribute("totalRevenue") %></strong></p>
        </div>

        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
