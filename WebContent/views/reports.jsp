<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> daily = (List<Map<String, Object>>) request.getAttribute("dailyAppointments");
    @SuppressWarnings("unchecked")
    Map<String, Double> revenue = (Map<String, Double>) request.getAttribute("revenueByTreatment");
    Double totalRevenueObj = (Double) request.getAttribute("totalRevenue");
    double totalRevenue = totalRevenueObj == null ? 0.0 : totalRevenueObj;

    int dailyCount = daily == null ? 0 : daily.size();
    double dailyRevenue = 0;
    int dailyCompleted = 0;
    if (daily != null) {
        for (Map<String, Object> row : daily) {
            Object fee = row.get("consultation_fee");
            if (fee instanceof Number) dailyRevenue += ((Number) fee).doubleValue();
            if ("Completed".equalsIgnoreCase(String.valueOf(row.get("status")))) dailyCompleted++;
        }
    }
%>
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

        <div class="stat-grid" style="grid-template-columns:repeat(4,minmax(0,1fr));">
            <div class="stat-card"><div class="stat-copy"><small>Appointments on <%= request.getAttribute("selectedDate") %></small><strong><%= dailyCount %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-check"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Completed that day</small><strong><%= dailyCompleted %></strong></div><span class="stat-icon"><i class="bi bi-clipboard2-check"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Billed that day (LKR)</small><strong><%= String.format("%,.0f", dailyRevenue) %></strong></div><span class="stat-icon"><i class="bi bi-cash-coin"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Total Revenue (LKR)</small><strong><%= String.format("%,.0f", totalRevenue) %></strong></div><span class="stat-icon"><i class="bi bi-graph-up-arrow"></i></span></div>
        </div>

        <div class="card">
            <h3 style="margin-top:0;"><i class="bi bi-calendar2-check"></i>&nbsp; Daily Appointments</h3>
            <form action="${pageContext.request.contextPath}/reports" method="get" style="display:flex; gap:12px; align-items:flex-end; margin-bottom: 18px;">
                <div class="field" style="flex:1; max-width:260px; margin-bottom:0;">
                    <label>Date</label>
                    <input type="date" class="form-control" name="date" value="<%= request.getAttribute("selectedDate") == null ? "" : request.getAttribute("selectedDate") %>" required />
                </div>
                <button type="submit" class="btn" style="width:auto; padding:11px 24px;"><i class="bi bi-search"></i> View</button>
            </form>

            <% if (daily == null || daily.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-calendar2-x"></i>No appointments on this date.</div>
            <% } else { %>
                <div class="table-wrap"><table class="data-table">
                    <thead>
                        <tr><th>Appointment</th><th>Patient</th><th>Dentist</th><th>Treatment</th><th>Fee</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                    <% for (Map<String, Object> row : daily) {
                        String status = String.valueOf(row.get("status"));
                        String statusClass = "Completed".equalsIgnoreCase(status) ? "active" : ("Cancelled".equalsIgnoreCase(status) ? "disabled" : "neutral");
                        Object feeObj = row.get("consultation_fee");
                        double fee = feeObj instanceof Number ? ((Number) feeObj).doubleValue() : 0;
                    %>
                        <tr>
                            <td><span class="id-chip"><%= row.get("appointment_number") %></span></td>
                            <td><strong><%= row.get("patient_name") %></strong></td>
                            <td><i class="bi bi-person-badge" style="color:var(--primary-dark); margin-right:5px;"></i><%= row.get("dentist_name") %></td>
                            <td><%= row.get("treatment_name") %></td>
                            <td>LKR <%= String.format("%,.2f", fee) %></td>
                            <td><span class="status-badge <%= statusClass %>"><%= status %></span></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table></div>
            <% } %>
        </div>

        <div class="card">
            <h3 style="margin-top:0;"><i class="bi bi-pie-chart"></i>&nbsp; Revenue by Treatment Type</h3>
            <% if (revenue == null || revenue.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-cash"></i>No billed revenue yet &mdash; generate a bill from an appointment to see the breakdown here.</div>
            <% } else { %>
                <div class="table-wrap"><table class="data-table">
                    <thead><tr><th>Treatment</th><th>Revenue (LKR)</th><th style="width:35%;">Share of Total</th></tr></thead>
                    <tbody>
                    <% for (String key : revenue.keySet()) {
                        double amount = revenue.get(key);
                        double pct = totalRevenue > 0 ? (amount / totalRevenue) * 100 : 0;
                    %>
                        <tr>
                            <td><i class="bi bi-clipboard2-pulse" style="color:var(--primary); margin-right:6px;"></i><%= key %></td>
                            <td><strong>LKR <%= String.format("%,.2f", amount) %></strong></td>
                            <td>
                                <div class="revenue-bar-track"><div class="revenue-bar-fill" style="width:<%= String.format("%.1f", pct) %>%;"></div></div>
                                <small style="color:var(--text-muted);"><%= String.format("%.1f", pct) %>%</small>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table></div>
            <% } %>
            <p style="margin-top:16px; font-size:15px; padding-top:14px; border-top:1px solid var(--line);">
                <strong><i class="bi bi-graph-up-arrow" style="color:var(--primary);"></i> Total Revenue: LKR <%= String.format("%,.2f", totalRevenue) %></strong>
            </p>
        </div>

        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
