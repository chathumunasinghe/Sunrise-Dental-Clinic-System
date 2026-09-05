<%@ page import="com.SunriseDental.Model.Patient" %>
<%@ page import="com.SunriseDental.Dao.NotificationDAO" %>
<%
    Patient currentPatient = (Patient) session.getAttribute("patient");
    String ctx = request.getContextPath();
    String currentUri = request.getRequestURI();
    int unreadCount = currentPatient != null ? new NotificationDAO().getUnreadCount(currentPatient.getPatientId()) : 0;
%>
<header class="patient-topbar">
    <a class="brand" href="<%= ctx %>/patientDashboard" style="color:#fff; text-decoration:none;">
        <span class="icon-circle">&#129463;</span>
        <span>Sunrise Dental Clinic</span>
    </a>
    <nav>
        <a class="<%= currentUri.contains("patientDashboard") ? "active" : "" %>" href="<%= ctx %>/patientDashboard"><i class="bi bi-grid-1x2"></i> My Appointments</a>
        <a class="<%= currentUri.contains("doctors") || currentUri.contains("bookAppointment") ? "active" : "" %>" href="<%= ctx %>/doctors"><i class="bi bi-person-badge"></i> Doctors</a>
        <a class="<%= currentUri.contains("treatments") ? "active" : "" %>" href="<%= ctx %>/treatments"><i class="bi bi-clipboard2-pulse"></i> Treatments &amp; Prices</a>
        <a class="<%= currentUri.contains("carePrecautions") ? "active" : "" %>" href="<%= ctx %>/views/carePrecautions.jsp"><i class="bi bi-shield-check"></i> Care Instructions</a>
        <a class="bell-link" href="<%= ctx %>/patientDashboard#notifications" title="Notifications">
            <i class="bi bi-bell"></i>
            <% if (unreadCount > 0) { %><span class="bell-badge"><%= unreadCount %></span><% } %>
        </a>
        <a class="logout" href="<%= ctx %>/logout" onclick="return confirm('Are you sure you want to log out?');"><i class="bi bi-box-arrow-right"></i> Logout</a>
    </nav>
</header>
