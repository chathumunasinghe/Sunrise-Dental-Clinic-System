<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Patient" %>
<%
    Patient loggedInPatient = (Patient) session.getAttribute("patient");
    if (loggedInPatient == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp?as=patient"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Receipt - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <%@ include file="includes/receiptStyles.jsp" %>
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>

    <div class="patient-page-wrapper">
        <div class="dashboard-heading">
            <div>
                <h1>My Receipt</h1>
                <p>Your appointment bill, ready to view or print.</p>
            </div>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("bill") != null && request.getAttribute("receipt") != null) { %>
            <%@ include file="includes/receiptCard.jsp" %>

            <div class="no-print" style="max-width:640px; margin: 16px auto 0; display:flex; gap:10px;">
                <button onclick="window.print()" class="btn" style="width:auto; padding:10px 22px;">
                    <i class="bi bi-printer"></i>&nbsp;Print Receipt
                </button>
                <% if (!bill.isPaid()) { %>
                <a class="btn btn-secondary" style="width:auto; padding:10px 22px;"
                   href="${pageContext.request.contextPath}/pay?appointmentNumber=<%= bill.getAppointmentNumber() %>">
                    <i class="bi bi-credit-card"></i>&nbsp;Pay Now
                </a>
                <% } %>
            </div>
        <% } %>

        <a class="back-link" style="display:block; margin-top:16px;" href="${pageContext.request.contextPath}/patientDashboard">&larr; Back to My Appointments</a>
    </div>
</body>
</html>
