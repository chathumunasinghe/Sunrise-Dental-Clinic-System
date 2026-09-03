<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Dentist" %>
<%
    Dentist dentist = (Dentist) request.getAttribute("dentist");
    String initial = (dentist == null || dentist.getName() == null || dentist.getName().isBlank())
            ? "D" : dentist.getName().replace("Dr.", "").trim().substring(0, 1).toUpperCase();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= dentist == null ? "Doctor Profile" : dentist.getName() %> - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <style>
        .profile-header {
            display: flex; align-items: center; gap: 22px; background: #fff; border: 1px solid var(--line);
            border-radius: 16px; padding: 26px; margin-bottom: 20px; box-shadow: 0 7px 20px rgba(15,41,58,.045);
            flex-wrap: wrap;
        }
        .profile-avatar {
            width: 92px; height: 92px; border-radius: 50%; flex-shrink: 0;
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: #fff; display: grid; place-items: center; font-size: 36px; font-weight: 800;
        }
        .profile-header h1 { margin: 0 0 4px; font-size: 21px; color: var(--navy); }
        .profile-header .specialization-tag {
            display: inline-block; background: var(--primary-light); color: var(--primary-dark);
            font-size: 12px; font-weight: 700; padding: 4px 12px; border-radius: 999px; margin-top: 2px;
        }
        .profile-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 18px; }
        @media (max-width: 800px) { .profile-grid { grid-template-columns: 1fr; } }
        .profile-section-title { display:flex; align-items:center; gap:8px; font-size:14.5px; font-weight:700; color:var(--navy); margin:0 0 10px; }
        .profile-section-title i { color: var(--primary-dark); font-size: 17px; }
        .profile-bio { font-size: 13.5px; line-height: 1.7; color: var(--text-dark); }
        .info-row { display: flex; justify-content: space-between; gap: 10px; padding: 10px 0; border-bottom: 1px dashed #eef1f2; font-size: 13px; }
        .info-row:last-child { border-bottom: none; }
        .info-row span:first-child { color: var(--text-muted); }
        .info-row span:last-child { font-weight: 600; color: var(--navy); text-align: right; }
    </style>
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">

        <% if (dentist == null) { %>
            <div class="alert alert-error">We couldn't find that doctor's profile.</div>
        <% } else { %>
            <div class="profile-header">
                <div class="profile-avatar"><%= initial %></div>
                <div>
                    <h1><%= dentist.getName() %></h1>
                    <span class="specialization-tag">
                        <i class="bi bi-award"></i>
                        <%= dentist.getSpecialization() == null || dentist.getSpecialization().isBlank() ? "General Dentistry" : dentist.getSpecialization() %>
                    </span>
                </div>
            </div>

            <% if (!dentist.isActive()) { %>
                <div class="alert alert-error"><i class="bi bi-exclamation-triangle"></i> This doctor is not currently accepting new appointments.</div>
            <% } %>

            <div class="profile-grid">
                <div class="card">
                    <div class="profile-section-title"><i class="bi bi-person-vcard"></i>About Dr. <%= dentist.getName().replace("Dr.", "").trim() %></div>
                    <p class="profile-bio">
                        <%= dentist.getBio() == null || dentist.getBio().isBlank()
                                ? "Full profile details for this doctor will be added soon."
                                : dentist.getBio() %>
                    </p>
                    <a class="btn" style="width:auto; padding:11px 22px; margin-top:8px;<%= dentist.isActive() ? "" : " opacity:.5; cursor:not-allowed;" %>"
                       href="${pageContext.request.contextPath}/bookAppointment?dentistId=<%= dentist.getDentistId() %>"
                       <%= dentist.isActive() ? "" : "onclick=\"return false;\" aria-disabled=\"true\"" %>>
                        <i class="bi bi-calendar-plus"></i> Book Appointment with Dr. <%= dentist.getName().replace("Dr.", "").trim() %>
                    </a>
                </div>

                <div class="card">
                    <div class="profile-section-title"><i class="bi bi-list-check"></i>At a Glance</div>
                    <div class="info-row">
                        <span>Qualification</span>
                        <span><%= dentist.getQualification() == null || dentist.getQualification().isBlank() ? "—" : dentist.getQualification() %></span>
                    </div>
                    <div class="info-row">
                        <span>Experience</span>
                        <span><%= dentist.getExperienceYears() == null ? "—" : dentist.getExperienceYears() + " years" %></span>
                    </div>
                    <div class="info-row">
                        <span>Specialization</span>
                        <span><%= dentist.getSpecialization() == null || dentist.getSpecialization().isBlank() ? "General Dentistry" : dentist.getSpecialization() %></span>
                    </div>
                    <div class="info-row">
                        <span>Consultation Hours</span>
                        <span><%= dentist.getConsultationDays() == null || dentist.getConsultationDays().isBlank() ? "—" : dentist.getConsultationDays() %></span>
                    </div>
                    <div class="info-row">
                        <span>Clinic Email</span>
                        <span><%= dentist.getEmail() == null || dentist.getEmail().isBlank() ? "—" : dentist.getEmail() %></span>
                    </div>
                </div>
            </div>
        <% } %>

        <a class="back-link" href="${pageContext.request.contextPath}/doctors">&larr; Back to All Doctors</a>
    </div>
</body>
</html>
