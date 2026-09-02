<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Dentist, com.SunriseDental.Model.TreatmentType, java.util.List" %>
<%
    Dentist dentist = (Dentist) request.getAttribute("dentist");
    @SuppressWarnings("unchecked")
    List<TreatmentType> treatmentTypes = (List<TreatmentType>) request.getAttribute("treatmentTypes");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Book Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="page-title">Book Appointment</div>
        <div class="page-subtitle">Choose a treatment, date and time</div>

        <div class="card">
            <% if (dentist != null) { %>
            <div class="booking-summary">
                <i class="bi bi-person-badge"></i>
                <div>
                    <strong><%= dentist.getName() %></strong><br/>
                    <span style="color:var(--text-muted);"><%= dentist.getSpecialization() == null || dentist.getSpecialization().isBlank() ? "General Dentistry" : dentist.getSpecialization() %></span>
                </div>
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/bookAppointment" method="post">
                <input type="hidden" name="dentistId" value="<%= dentist == null ? "" : dentist.getDentistId() %>" />
                <div class="field">
                    <label>Treatment Type</label>
                    <select name="treatmentId" class="form-select" required>
                        <% if (treatmentTypes != null) { for (TreatmentType t : treatmentTypes) { %>
                            <option value="<%= t.getTreatmentId() %>"><%= t.getTreatmentName() %> &mdash; LKR <%= String.format("%,.2f", t.getConsultationFee()) %></option>
                        <% }} %>
                    </select>
                </div>
                <div class="form-row">
                    <div class="field">
                        <label>Appointment Date</label>
                        <input type="date" class="form-control" name="date" required />
                    </div>
                    <div class="field">
                        <label>Appointment Time</label>
                        <input type="time" class="form-control" name="time" required />
                    </div>
                </div>
                <button type="submit" class="btn"><i class="bi bi-calendar-check"></i> Confirm &amp; Continue to Payment</button>
            </form>
        </div>
        <a class="back-link" href="${pageContext.request.contextPath}/doctors">&larr; Back to Doctors</a>
    </div>
</body>
</html>
