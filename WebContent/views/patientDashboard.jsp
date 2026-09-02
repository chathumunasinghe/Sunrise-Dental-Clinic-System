<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Patient, java.util.List, java.util.Map" %>
<%
    Patient loggedInPatient = (Patient) session.getAttribute("patient");
    if (loggedInPatient == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> myAppointments = (List<Map<String,Object>>) request.getAttribute("myAppointments");
    int total = myAppointments == null ? 0 : myAppointments.size();
    int upcoming = 0;
    if (myAppointments != null) {
        for (Map<String,Object> a : myAppointments) {
            if ("Scheduled".equalsIgnoreCase(String.valueOf(a.get("status")))) upcoming++;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Appointments - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="dashboard-heading">
            <div>
                <h1>Hello, <%= loggedInPatient.getName() %></h1>
                <p>Here's an overview of your appointments with us.</p>
            </div>
            <a class="btn" style="width:auto;padding:11px 20px;" href="${pageContext.request.contextPath}/doctors"><i class="bi bi-calendar-plus"></i> Book New Appointment</a>
        </div>

        <div class="stat-grid" style="grid-template-columns:repeat(2,minmax(0,1fr));">
            <div class="stat-card"><div class="stat-copy"><small>Total Appointments</small><strong><%= total %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-check"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Upcoming</small><strong><%= upcoming %></strong></div><span class="stat-icon"><i class="bi bi-clock-history"></i></span></div>
        </div>

        <div class="card">
            <h3 style="margin-top:0;">My Appointments</h3>
            <% if (myAppointments == null || myAppointments.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-calendar2"></i>You haven't booked an appointment yet.</div>
            <% } else { %>
            <div class="table-wrap"><table class="data-table">
                <thead><tr><th>Appointment</th><th>Doctor</th><th>Treatment</th><th>Date &amp; Time</th><th>Status</th><th>Payment</th></tr></thead>
                <tbody>
                <% for (Map<String,Object> a : myAppointments) {
                    String status = String.valueOf(a.get("status"));
                    String statusClass = "Completed".equalsIgnoreCase(status) ? "active" : ("Cancelled".equalsIgnoreCase(status) ? "disabled" : "neutral");
                    boolean hasBill = Boolean.TRUE.equals(a.get("has_bill"));
                    boolean paid = Boolean.TRUE.equals(a.get("paid"));
                %>
                    <tr>
                        <td><span class="id-chip"><%= a.get("appointment_number") %></span></td>
                        <td><%= a.get("dentist_name") %><br><small style="color:#94a3b8;"><%= a.get("specialization") == null || String.valueOf(a.get("specialization")).isBlank() ? "General Dentistry" : a.get("specialization") %></small></td>
                        <td><%= a.get("treatment_name") %><br><small style="color:#94a3b8;">LKR <%= String.format("%,.2f", ((Number) a.get("consultation_fee")).doubleValue()) %></small></td>
                        <td><%= a.get("appointment_date") %><br><small style="color:#94a3b8;"><%= a.get("appointment_time") %></small></td>
                        <td><span class="status-badge <%= statusClass %>"><%= status %></span></td>
                        <td>
                            <% if (!hasBill) { %>
                                <span style="color:var(--text-muted); font-size:12px;">&mdash;</span>
                            <% } else if (paid) { %>
                                <a class="paid-badge" style="text-decoration:none;" href="${pageContext.request.contextPath}/pay?appointmentNumber=<%= a.get("appointment_number") %>"><i class="bi bi-check-circle-fill"></i> Paid &middot; <span style="text-decoration:underline;">View Receipt</span></a>
                            <% } else { %>
                                <a class="btn inline-btn" href="${pageContext.request.contextPath}/pay?appointmentNumber=<%= a.get("appointment_number") %>"><i class="bi bi-credit-card"></i> Pay Now</a>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table></div>
            <% } %>
        </div>
    </div>
</body>
</html>
