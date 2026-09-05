<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> daily = (List<Map<String, Object>>) request.getAttribute("dailyAppointments");
    @SuppressWarnings("unchecked")
    Map<String, Double> paymentMethodRevenue = (Map<String, Double>) request.getAttribute("paymentMethodRevenue");
    @SuppressWarnings("unchecked")
    Map<String, Object> pending = (Map<String, Object>) request.getAttribute("pendingPayments");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> byDentist = (List<Map<String, Object>>) request.getAttribute("appointmentsByDentist");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> pendingDetails = (List<Map<String, Object>>) request.getAttribute("pendingPaymentDetails");

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

    double cashTotal = paymentMethodRevenue != null && paymentMethodRevenue.containsKey("CASH") ? paymentMethodRevenue.get("CASH") : 0;
    double onlineTotal = paymentMethodRevenue != null && paymentMethodRevenue.containsKey("ONLINE") ? paymentMethodRevenue.get("ONLINE") : 0;
    double collectedTotal = cashTotal + onlineTotal;
    double cashPct = collectedTotal > 0 ? (cashTotal / collectedTotal) * 100 : 0;
    double onlinePct = collectedTotal > 0 ? (onlineTotal / collectedTotal) * 100 : 0;

    long pendingCount = pending != null && pending.get("count") != null ? ((Number) pending.get("count")).longValue() : 0;
    double pendingTotal = pending != null && pending.get("total") != null ? ((Number) pending.get("total")).doubleValue() : 0;

    int maxDentistCount = 1;
    if (byDentist != null) {
        for (Map<String, Object> row : byDentist) {
            int c = ((Number) row.get("appt_count")).intValue();
            if (c > maxDentistCount) maxDentistCount = c;
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
        <div class="page-subtitle">Daily appointments, payments collected, and doctor workload &middot; <span class="role-badge solid-admin">Admin only</span></div>

        <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><%= request.getAttribute("error") %></div><% } %>

        <div class="stat-grid" style="grid-template-columns:repeat(4,minmax(0,1fr));">
            <div class="stat-card"><div class="stat-copy"><small>Appointments on <%= request.getAttribute("selectedDate") %></small><strong><%= dailyCount %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-check"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Completed that day</small><strong><%= dailyCompleted %></strong></div><span class="stat-icon"><i class="bi bi-clipboard2-check"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Expected (LKR)</small><strong><%= String.format("%,.0f", dailyRevenue) %></strong></div><span class="stat-icon"><i class="bi bi-cash-coin"></i></span></div>
            <div class="stat-card stat-card-link" onclick="document.getElementById('pending-payments').scrollIntoView({behavior:'smooth'});" role="link" tabindex="0" onkeypress="if(event.key==='Enter'){this.click();}">
                <div class="stat-copy"><small>Pending Payments</small><strong><%= pendingCount %></strong></div>
                <span class="stat-icon"><i class="bi bi-hourglass-split"></i></span>
            </div>
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

        <div class="report-grid">
            <div class="card">
                <h3 style="margin-top:0;"><i class="bi bi-cash-stack"></i>&nbsp; Payments Collected by Method</h3>
                <% if (collectedTotal <= 0) { %>
                    <div class="empty-state"><i class="bi bi-cash"></i>No payments collected yet &mdash; totals appear here once bills are marked paid, online or in cash.</div>
                <% } else { %>
                    <div class="table-wrap"><table class="data-table">
                        <thead><tr><th>Method</th><th>Collected (LKR)</th><th style="width:35%;">Share</th></tr></thead>
                        <tbody>
                            <tr>
                                <td><i class="bi bi-cash-coin" style="color:var(--success); margin-right:6px;"></i>Cash</td>
                                <td><strong>LKR <%= String.format("%,.2f", cashTotal) %></strong></td>
                                <td>
                                    <div class="revenue-bar-track"><div class="revenue-bar-fill" style="width:<%= String.format("%.1f", cashPct) %>%;"></div></div>
                                    <small style="color:var(--text-muted);"><%= String.format("%.1f", cashPct) %>%</small>
                                </td>
                            </tr>
                            <tr>
                                <td><i class="bi bi-credit-card" style="color:var(--primary); margin-right:6px;"></i>Online</td>
                                <td><strong>LKR <%= String.format("%,.2f", onlineTotal) %></strong></td>
                                <td>
                                    <div class="revenue-bar-track"><div class="revenue-bar-fill" style="width:<%= String.format("%.1f", onlinePct) %>%;"></div></div>
                                    <small style="color:var(--text-muted);"><%= String.format("%.1f", onlinePct) %>%</small>
                                </td>
                            </tr>
                        </tbody>
                    </table></div>
                    <p style="margin-top:16px; font-size:14px; padding-top:14px; border-top:1px solid var(--line);">
                        <strong><i class="bi bi-graph-up-arrow" style="color:var(--primary);"></i> Total Collected: LKR <%= String.format("%,.2f", collectedTotal) %></strong>
                    </p>
                <% } %>
                <% if (pendingCount > 0) { %>
                    <a href="#pending-payments" class="alert alert-error" style="display:block; margin-top:14px; margin-bottom:0; text-decoration:none;">
                        <i class="bi bi-exclamation-triangle"></i> <%= pendingCount %> bill<%= pendingCount == 1 ? "" : "s" %> still unpaid, totalling LKR <%= String.format("%,.2f", pendingTotal) %>. <span style="text-decoration:underline;">View who owes &rarr;</span>
                    </a>
                <% } %>
            </div>

            <div class="card">
                <h3 style="margin-top:0;"><i class="bi bi-people"></i>&nbsp; Doctor Workload &mdash; <%= request.getAttribute("selectedDate") %></h3>
                <% if (byDentist == null || byDentist.isEmpty()) { %>
                    <div class="empty-state"><i class="bi bi-person-x"></i>No appointments assigned to any doctor on this date.</div>
                <% } else { %>
                    <div style="display:flex; flex-direction:column; gap:12px;">
                    <% for (Map<String, Object> row : byDentist) {
                        int count = ((Number) row.get("appt_count")).intValue();
                        double widthPct = (count * 100.0) / maxDentistCount;
                    %>
                        <div>
                            <div style="display:flex; justify-content:space-between; font-size:13px; margin-bottom:5px;">
                                <span><i class="bi bi-person-badge" style="color:var(--primary-dark); margin-right:5px;"></i><%= row.get("dentist_name") %></span>
                                <strong><%= count %> appt<%= count == 1 ? "" : "s" %></strong>
                            </div>
                            <div class="revenue-bar-track"><div class="revenue-bar-fill" style="width:<%= String.format("%.1f", widthPct) %>%;"></div></div>
                        </div>
                    <% } %>
                    </div>
                <% } %>
            </div>
        </div>

        <div class="card" id="pending-payments">
            <h3 style="margin-top:0;"><i class="bi bi-hourglass-split"></i>&nbsp; Patients With Pending Payments</h3>
            <% if (pendingDetails == null || pendingDetails.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-check2-circle"></i>Nothing outstanding &mdash; every issued bill has been paid.</div>
            <% } else { %>
                <div class="table-wrap"><table class="data-table">
                    <thead><tr><th>Appointment</th><th>Patient</th><th>Contact</th><th>Treatment</th><th>Appointment Date</th><th>Amount Due</th><th></th></tr></thead>
                    <tbody>
                    <% for (Map<String, Object> row : pendingDetails) { %>
                        <tr>
                            <td><span class="id-chip"><%= row.get("appointment_number") %></span></td>
                            <td><strong><%= row.get("patient_name") %></strong></td>
                            <td><%= row.get("contact_number") == null || String.valueOf(row.get("contact_number")).isBlank() ? "—" : row.get("contact_number") %></td>
                            <td><%= row.get("treatment_name") %></td>
                            <td><%= row.get("appointment_date") %></td>
                            <td><strong style="color:var(--danger);">LKR <%= String.format("%,.2f", ((Number) row.get("total_amount")).doubleValue()) %></strong></td>
                            <td>
                                <a class="btn btn-secondary inline-btn" style="text-decoration:none; white-space:nowrap;"
                                   href="${pageContext.request.contextPath}/billing?appointmentNumber=<%= row.get("appointment_number") %>">
                                    <i class="bi bi-receipt"></i> Collect Payment
                                </a>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table></div>
            <% } %>
        </div>

        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
