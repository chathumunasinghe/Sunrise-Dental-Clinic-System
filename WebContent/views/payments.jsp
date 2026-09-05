<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Staff, java.util.List, java.util.Map" %>
<%
    Staff loggedInStaff = (Staff) session.getAttribute("staff");
    if (loggedInStaff == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    if (loggedInStaff.isDentist()) { response.sendRedirect(request.getContextPath() + "/dentistDashboard"); return; }

    @SuppressWarnings("unchecked") List<Map<String,Object>> bills = (List<Map<String,Object>>) request.getAttribute("bills");
    String search = (String) request.getAttribute("search");
    String filter = (String) request.getAttribute("filter");
    if (filter == null) filter = "all";

    int paidCount = 0, unpaidCount = 0;
    double unpaidTotal = 0;
    if (bills != null) {
        for (Map<String,Object> b : bills) {
            boolean paid = Boolean.TRUE.equals(b.get("paid"));
            if (paid) paidCount++; else { unpaidCount++; unpaidTotal += ((Number) b.get("total_amount")).doubleValue(); }
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Payments - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <style>
        .payment-filters { display:flex; gap:8px; flex-wrap:wrap; margin-bottom:16px; }
        .payment-filter {
            text-decoration:none; font-size:12.5px; font-weight:600; color:var(--primary-dark);
            background:var(--primary-light); border-radius:999px; padding:8px 16px;
        }
        .payment-filter.active { background:var(--primary-dark); color:#fff; }
    </style>
</head>
<body>
<%@ include file="includes/topbar.jsp" %>
<div class="page-wrapper">
    <div class="page-title">Payments</div>
    <div class="page-subtitle">See who has paid and who still owes, without needing the exact appointment number.</div>

    <div class="stat-grid" style="grid-template-columns:repeat(3,minmax(0,1fr));">
        <div class="stat-card"><div class="stat-copy"><small>Total Bills</small><strong><%= bills == null ? 0 : bills.size() %></strong></div><span class="stat-icon"><i class="bi bi-receipt"></i></span></div>
        <div class="stat-card"><div class="stat-copy"><small>Paid</small><strong><%= paidCount %></strong></div><span class="stat-icon"><i class="bi bi-check-circle"></i></span></div>
        <div class="stat-card"><div class="stat-copy"><small>Unpaid</small><strong><%= unpaidCount %></strong><small style="color:#94a3b8;">LKR <%= String.format("%,.2f", unpaidTotal) %> owed</small></div><span class="stat-icon"><i class="bi bi-hourglass-split"></i></span></div>
    </div>

    <div class="card">
        <form action="${pageContext.request.contextPath}/payments" method="get" style="display:flex; gap:12px; align-items:flex-end; flex-wrap:wrap; margin-bottom:14px;">
            <input type="hidden" name="filter" value="<%= filter %>" />
            <div class="field" style="flex:1; min-width:220px; margin-bottom:0;">
                <label>Search</label>
                <input type="text" class="form-control" name="search" placeholder="Patient name, contact number, or appointment number"
                       value="<%= search == null ? "" : search %>" />
            </div>
            <button type="submit" class="btn" style="width:auto; padding:11px 22px;"><i class="bi bi-search"></i> Search</button>
            <% if (search != null && !search.isEmpty()) { %>
            <a class="btn btn-secondary" style="width:auto; padding:11px 22px;" href="${pageContext.request.contextPath}/payments?filter=<%= filter %>">Clear</a>
            <% } %>
        </form>

        <div class="payment-filters">
            <a class="payment-filter <%= "all".equals(filter) ? "active" : "" %>" href="${pageContext.request.contextPath}/payments?filter=all&search=<%= search == null ? "" : search %>">All (<%= bills == null ? 0 : bills.size() %>)</a>
            <a class="payment-filter <%= "unpaid".equals(filter) ? "active" : "" %>" href="${pageContext.request.contextPath}/payments?filter=unpaid&search=<%= search == null ? "" : search %>">Unpaid (<%= unpaidCount %>)</a>
            <a class="payment-filter <%= "paid".equals(filter) ? "active" : "" %>" href="${pageContext.request.contextPath}/payments?filter=paid&search=<%= search == null ? "" : search %>">Paid (<%= paidCount %>)</a>
        </div>

        <div class="table-wrap"><table class="data-table">
            <thead><tr><th>Appointment</th><th>Patient</th><th>Contact</th><th>Treatment</th><th>Date</th><th>Amount</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <% boolean anyRow = false;
               if (bills != null) { for (Map<String,Object> b : bills) {
                boolean paid = Boolean.TRUE.equals(b.get("paid"));
                if ("paid".equals(filter) && !paid) continue;
                if ("unpaid".equals(filter) && paid) continue;
                anyRow = true;
                String method = (String) b.get("payment_method");
            %>
                <tr>
                    <td><span class="id-chip"><%= b.get("appointment_number") %></span></td>
                    <td><strong><%= b.get("patient_name") %></strong></td>
                    <td><%= b.get("contact_number") %></td>
                    <td><%= b.get("treatment_name") %></td>
                    <td><%= b.get("appointment_date") %></td>
                    <td>LKR <%= String.format("%,.2f", ((Number) b.get("total_amount")).doubleValue()) %></td>
                    <td>
                        <% if (paid) { %>
                            <span class="status-badge active">PAID<%= method == null ? "" : " &middot; " + method %></span>
                        <% } else { %>
                            <span class="status-badge pending">UNPAID</span>
                        <% } %>
                    </td>
                    <td>
                        <a class="btn btn-secondary inline-btn" style="text-decoration:none; display:inline-block;"
                           href="${pageContext.request.contextPath}/billing?appointmentNumber=<%= b.get("appointment_number") %>">
                            <i class="bi bi-receipt-cutoff"></i> <%= paid ? "View Receipt" : "Collect Payment" %>
                        </a>
                    </td>
                </tr>
            <% }} if (!anyRow) { %>
                <tr><td colspan="8" style="text-align:center;color:#94a3b8;padding:28px">No bills match this filter.</td></tr>
            <% } %>
            </tbody>
        </table></div>
    </div>
    <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
</div>
</body>
</html>
