<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Dentist, java.util.List" %>
<%
    @SuppressWarnings("unchecked")
    List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Meet Our Doctors - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="page-title">Meet Our Doctors</div>
        <div class="page-subtitle">Choose a doctor to book your appointment</div>

        <div class="doctor-grid">
            <% if (dentists != null) { for (Dentist d : dentists) {
                String initial = d.getName() == null || d.getName().isBlank() ? "D" : d.getName().replace("Dr.", "").trim().substring(0,1).toUpperCase();
            %>
                <div class="doctor-card">
                    <div class="doctor-avatar"><%= initial %></div>
                    <h3><%= d.getName() %></h3>
                    <p><%= d.getSpecialization() == null || d.getSpecialization().isBlank() ? "General Dentistry" : d.getSpecialization() %></p>
                    <a class="btn" href="${pageContext.request.contextPath}/bookAppointment?dentistId=<%= d.getDentistId() %>">
                        <i class="bi bi-calendar-plus"></i> Book Appointment
                    </a>
                </div>
            <% }} %>
        </div>
    </div>
</body>
</html>
