<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Staff, com.SunriseDental.Model.Dentist, java.util.List" %>
<%
    Staff loggedInStaff = (Staff) session.getAttribute("staff");
    if (loggedInStaff == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    if (!loggedInStaff.isAdmin()) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
    @SuppressWarnings("unchecked") List<Dentist> dentists = (List<Dentist>) request.getAttribute("dentists");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Doctors - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
<%@ include file="includes/topbar.jsp" %>
<div class="page-wrapper">
    <div class="page-title">Manage Doctors</div>
    <div class="page-subtitle">Enable or disable a doctor's availability for new patient bookings.</div>

    <% if (request.getAttribute("message") != null) { %><div class="alert alert-success"><%= request.getAttribute("message") %></div><% } %>
    <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><%= request.getAttribute("error") %></div><% } %>

    <div class="card">
        <div class="staff-toolbar">
            <div><h3><i class="bi bi-person-badge"></i>&nbsp; Doctor Directory</h3>
            <p><%= dentists == null ? 0 : dentists.size() %> doctor(s) on record. Disabling a doctor hides them from the patient booking pages and front-desk "New Appointment" form — it does not affect their existing appointment history.</p></div>
            <span class="role-badge solid-admin"><i class="bi bi-shield-lock"></i> Admin only</span>
        </div>
        <div class="table-wrap"><table class="data-table">
            <thead><tr><th>Doctor</th><th>Specialization</th><th>Qualification</th><th>Experience</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <% if (dentists != null && !dentists.isEmpty()) { for (Dentist d : dentists) {
                String docInitial = d.getName() == null || d.getName().isBlank() ? "D" : d.getName().replace("Dr.", "").trim().substring(0,1).toUpperCase();
                boolean active = d.isActive();
            %>
                <tr>
                    <td><span class="staff-avatar"><%= docInitial %></span><strong><%= d.getName() %></strong></td>
                    <td><%= d.getSpecialization() == null || d.getSpecialization().isBlank() ? "General Dentistry" : d.getSpecialization() %></td>
                    <td><%= d.getQualification() == null || d.getQualification().isBlank() ? "—" : d.getQualification() %></td>
                    <td><%= d.getExperienceYears() == null ? "—" : d.getExperienceYears() + " yrs" %></td>
                    <td><span class="status-badge <%= active ? "active" : "disabled" %>"><%= active ? "ACTIVE" : "DISABLED" %></span></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/manageDoctors" method="post" style="display:inline">
                            <input type="hidden" name="dentistId" value="<%= d.getDentistId() %>">
                            <input type="hidden" name="newStatus" value="<%= active ? "DISABLED" : "ACTIVE" %>">
                            <button class="btn btn-secondary inline-btn" type="submit">
                                <i class="bi <%= active ? "bi-person-x" : "bi-person-check" %>"></i><%= active ? "Disable" : "Enable" %>
                            </button>
                        </form>
                    </td>
                </tr>
            <% }} else { %><tr><td colspan="6" style="text-align:center;color:#94a3b8;padding:28px">No doctors found.</td></tr><% } %>
            </tbody>
        </table></div>
    </div>
    <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
</div>
</body>
</html>
