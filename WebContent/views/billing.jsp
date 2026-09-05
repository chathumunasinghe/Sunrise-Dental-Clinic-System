<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Billing - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <%@ include file="includes/receiptStyles.jsp" %>
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Calculate &amp; Print Bill</div>
        <div class="page-subtitle">Generate a full receipt for a completed appointment</div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/billing" method="get" style="display:flex; gap:12px; align-items:flex-end;">
                <div class="field" style="flex:1; margin-bottom:0;">
                    <label>Appointment Number</label>
                    <input type="text" class="form-control" name="appointmentNumber" placeholder="e.g. APT0001" required />
                </div>
                <button type="submit" class="btn" style="width:auto; padding:11px 24px;">Generate Bill</button>
            </form>
        </div>

        <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("message") %></div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("bill") != null && request.getAttribute("receipt") != null) { %>
            <%@ include file="includes/receiptCard.jsp" %>

            <div class="no-print" style="max-width:640px; margin: 16px auto 0; display:flex; gap:10px; flex-wrap:wrap;">
                <button onclick="window.print()" class="btn" style="width:auto; padding:10px 22px;">
                    <i class="bi bi-printer"></i>&nbsp;Print Receipt
                </button>
                <% if (!bill.isPaid()) { %>
                <a class="btn btn-secondary" style="width:auto; padding:10px 22px;"
                   href="${pageContext.request.contextPath}/pay?appointmentNumber=<%= bill.getAppointmentNumber() %>">
                    <i class="bi bi-credit-card"></i>&nbsp;Pay Online
                </a>
                <form action="${pageContext.request.contextPath}/markCashPaid" method="post" style="display:inline"
                      onsubmit="return confirm('Confirm cash payment of LKR <%= String.format("%,.2f", bill.getTotalAmount()) %> received for <%= bill.getAppointmentNumber() %>?');">
                    <input type="hidden" name="appointmentNumber" value="<%= bill.getAppointmentNumber() %>" />
                    <button type="submit" class="btn" style="width:auto; padding:10px 22px; background:var(--success);">
                        <i class="bi bi-cash-coin"></i>&nbsp;Mark as Paid (Cash)
                    </button>
                </form>
                <% } %>
            </div>
        <% } %>
        <br/>
        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
