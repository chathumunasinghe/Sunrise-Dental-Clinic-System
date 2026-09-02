<%@ page import="com.SunriseDental.Model.Patient" %>
<%
    Patient currentPatient = (Patient) session.getAttribute("patient");
    String ctx = request.getContextPath();
    String currentUri = request.getRequestURI();
%>
<header class="patient-topbar">
    <a class="brand" href="<%= ctx %>/patientDashboard" style="color:#fff; text-decoration:none;">
        <span class="icon-circle">&#129463;</span>
        <span>Sunrise Dental Clinic</span>
    </a>
    <nav>
        <a class="<%= currentUri.contains("patientDashboard") ? "active" : "" %>" href="<%= ctx %>/patientDashboard"><i class="bi bi-grid-1x2"></i> My Appointments</a>
        <a class="<%= currentUri.contains("doctors") || currentUri.contains("bookAppointment") ? "active" : "" %>" href="<%= ctx %>/doctors"><i class="bi bi-person-badge"></i> Doctors</a>
        <a class="logout" href="<%= ctx %>/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
    </nav>
</header>
