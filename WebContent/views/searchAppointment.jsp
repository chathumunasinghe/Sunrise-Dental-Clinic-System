<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Appointment" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Search Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Search Appointment</div>
        <div class="page-subtitle">Look up an appointment by its number</div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/searchAppointment" method="get" style="display:flex; gap:12px; align-items:flex-end;">
                <div class="field" style="flex:1; margin-bottom:0;">
                    <label>Appointment Number</label>
                    <input type="text" class="form-control" name="appointmentNumber" placeholder="e.g. APT0001" required />
                </div>
                <button type="submit" class="btn" style="width:auto; padding:11px 24px;">Search</button>
            </form>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% Appointment a = (Appointment) request.getAttribute("appointment");
           if (a != null) { %>
            <div class="card">
                <table class="data-table">
                    <tr><th>Appointment Number</th><td><%= a.getAppointmentNumber() %></td></tr>
                    <tr><th>Patient ID</th><td><%= a.getPatientId() %></td></tr>
                    <tr><th>Dentist ID</th><td><%= a.getDentistId() %></td></tr>
                    <tr><th>Treatment ID</th><td><%= a.getTreatmentId() %></td></tr>
                    <tr><th>Date</th><td><%= a.getAppointmentDate() %></td></tr>
                    <tr><th>Time</th><td><%= a.getAppointmentTime() %></td></tr>
                    <tr><th>Status</th><td><%= a.getStatus() %></td></tr>
                </table>
                <form action="${pageContext.request.contextPath}/updateAppointmentStatus" method="post" style="display:flex; gap:10px; align-items:flex-end; margin-top:18px;">
                    <input type="hidden" name="appointmentNumber" value="<%= a.getAppointmentNumber() %>" />
                    <input type="hidden" name="returnTo" value="search" />
                    <div class="field" style="margin-bottom:0;">
                        <label>Update Status</label>
                        <select name="status" class="form-select">
                            <option value="Scheduled" <%= "Scheduled".equalsIgnoreCase(a.getStatus()) ? "selected" : "" %>>Scheduled</option>
                            <option value="Completed" <%= "Completed".equalsIgnoreCase(a.getStatus()) ? "selected" : "" %>>Met doctor / Completed</option>
                            <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(a.getStatus()) ? "selected" : "" %>>Cancelled</option>
                        </select>
                    </div>
                    <button type="submit" class="btn" style="width:auto; padding:11px 22px;">Save Status</button>
                </form>
            </div>
        <% } %>
        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
