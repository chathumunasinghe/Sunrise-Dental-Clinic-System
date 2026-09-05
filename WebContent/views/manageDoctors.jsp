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
    <div class="page-subtitle">Create doctor accounts and control their availability for new patient bookings.</div>

    <% if (request.getAttribute("message") != null) { %><div class="alert alert-success"><%= request.getAttribute("message") %></div><% } %>
    <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><%= request.getAttribute("error") %></div><% } %>

    <div class="card">
        <div class="staff-toolbar"><div><h3><i class="bi bi-person-plus"></i>&nbsp; Create Doctor Account</h3><p>Creates the doctor's profile and their login in one step.</p></div><span class="role-badge solid-admin"><i class="bi bi-shield-lock"></i> Admin only</span></div>
        <form action="${pageContext.request.contextPath}/manageDoctors" method="post">
            <input type="hidden" name="action" value="addDoctor" />
            <div class="form-row">
                <div class="field"><label>Full Name</label><input type="text" class="form-control" name="name" placeholder="e.g. Dr. Amara Perera" required maxlength="100" /></div>
                <div class="field"><label>Specialization</label><input type="text" class="form-control" name="specialization" placeholder="e.g. Orthodontics" maxlength="100" /></div>
            </div>
            <div class="form-row">
                <div class="field"><label>Qualification</label><input type="text" class="form-control" name="qualification" placeholder="e.g. BDS, MSc (Ortho)" maxlength="150" /></div>
                <div class="field"><label>Years of Experience</label><input type="number" class="form-control" name="experienceYears" min="0" max="60" placeholder="e.g. 8" /></div>
            </div>
            <div class="field"><label>Short Bio</label><textarea class="form-control" name="bio" rows="2" maxlength="500" placeholder="A sentence or two for the patient-facing profile"></textarea></div>
            <div class="field"><label>Consultation Days &amp; Hours</label><input type="text" class="form-control" name="consultationDays" placeholder="e.g. Mon, Wed, Fri — 9:00 AM to 4:00 PM" maxlength="150" /></div>
            <div class="form-row">
                <div class="field"><label>Email Address</label><input type="email" class="form-control" name="email" placeholder="doctor@sunrisedental.lk" required maxlength="100" /></div>
                <div class="field"><label>Login Username</label><input type="text" class="form-control" name="username" placeholder="Unique login username" required maxlength="50" pattern="[A-Za-z0-9._-]{3,50}" /></div>
            </div>
            <div class="field" style="max-width:320px"><label>Temporary Password</label><input type="password" class="form-control" name="password" placeholder="Minimum 6 characters" required pattern=".{6,}" title="Minimum 6 characters" /></div>
            <button type="submit" class="btn inline-btn" style="padding:10px 18px"><i class="bi bi-plus-circle"></i>Create Doctor Account</button>
        </form>
    </div>

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
