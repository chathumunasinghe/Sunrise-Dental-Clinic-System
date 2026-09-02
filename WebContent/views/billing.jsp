<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Bill" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Billing - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <style>
        @media print {
            .topbar, form, .back-link, .no-print { display: none; }
            .page-wrapper { margin: 0; }
        }
    </style>
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Calculate &amp; Print Bill</div>
        <div class="page-subtitle">Generate a receipt for a completed appointment</div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/billing" method="get" style="display:flex; gap:12px; align-items:flex-end;">
                <div class="field" style="flex:1; margin-bottom:0;">
                    <label>Appointment Number</label>
                    <input type="text" class="form-control" name="appointmentNumber" placeholder="e.g. APT0001" required />
                </div>
                <button type="submit" class="btn" style="width:auto; padding:11px 24px;">Generate Bill</button>
            </form>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% Bill bill = (Bill) request.getAttribute("bill");
           if (bill != null) { %>
            <div class="receipt">
                <h3 style="display:flex; align-items:center; gap:8px;">
                    &#129468;
                    Sunrise Dental Clinic
                </h3>
                <p style="margin:4px 0;"><strong>Appointment:</strong> <%= bill.getAppointmentNumber() %></p>
                <p style="margin:4px 0;"><strong>Total Amount:</strong> LKR <%= bill.getTotalAmount() %></p>
            </div>
            <button onclick="window.print()" class="btn no-print" style="width:auto; padding:10px 22px; margin-top:14px;">&#128424; Print Receipt</button>
        <% } %>
        <br/>
        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
