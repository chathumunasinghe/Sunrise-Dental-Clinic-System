<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Staff, java.util.List, java.util.Map" %>
<%
    Staff loggedInStaff = (Staff) session.getAttribute("staff");
    if (loggedInStaff == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    if (!loggedInStaff.isAdmin()) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }

    @SuppressWarnings("unchecked") List<Map<String,Object>> patientList = (List<Map<String,Object>>) request.getAttribute("patientList");
    @SuppressWarnings("unchecked") List<Map<String,Object>> historyList = (List<Map<String,Object>>) request.getAttribute("historyList");

    int totalPatients = patientList == null ? 0 : patientList.size();
    int totalVisits = historyList == null ? 0 : historyList.size();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Patients - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
<%@ include file="includes/topbar.jsp" %>
<div class="page-wrapper">
    <div class="page-title">Patients</div>
    <div class="page-subtitle">A single, professional view of every patient together with their assigned doctor and treatment history.</div>

    <% if (request.getParameter("message") != null) { %><div class="alert alert-success"><%= request.getParameter("message") %></div><% } %>
    <% if (request.getParameter("error") != null) { %><div class="alert alert-error"><%= request.getParameter("error") %></div><% } %>

    <div class="stat-grid" style="grid-template-columns:repeat(2,minmax(0,1fr));">
        <div class="stat-card"><div class="stat-copy"><small>Total Patients</small><strong><%= totalPatients %></strong></div><span class="stat-icon"><i class="bi bi-people"></i></span></div>
        <div class="stat-card"><div class="stat-copy"><small>Total Visits</small><strong><%= totalVisits %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-check"></i></span></div>
    </div>

    <div class="card">
        <div class="staff-toolbar">
            <div><h3><i class="bi bi-people"></i>&nbsp; Patient Directory</h3><p><%= totalPatients %> patient(s) registered in the system.</p></div>
            <div class="table-search"><i class="bi bi-search"></i><input type="text" id="patientSearch" placeholder="Search by name, ID or contact..." onkeyup="filterTable('patientSearch','patientTable')" /></div>
        </div>
        <div class="table-wrap"><table class="data-table" id="patientTable">
            <thead><tr><th>Patient</th><th>Patient ID</th><th>Contact Number</th><th>Email</th><th>Address</th><th>Visits</th><th>Last Visit</th><th>Action</th></tr></thead>
            <tbody>
            <% if (patientList != null && !patientList.isEmpty()) { for (Map<String,Object> p : patientList) {
                String name = String.valueOf(p.get("name"));
                String patientInitial = name.isBlank() ? "P" : name.substring(0,1).toUpperCase();
                Object lastVisit = p.get("last_visit");
                int visits = p.get("visit_count") == null ? 0 : ((Number) p.get("visit_count")).intValue();
            %>
                <tr>
                    <td><span class="staff-avatar"><%= patientInitial %></span><strong><%= name %></strong></td>
                    <td><span class="id-chip"><%= p.get("patient_id") %></span></td>
                    <td><%= p.get("contact_number") == null || String.valueOf(p.get("contact_number")).isBlank() ? "—" : p.get("contact_number") %></td>
                    <td><%= p.get("email") == null || String.valueOf(p.get("email")).isBlank() ? "—" : p.get("email") %></td>
                    <td><%= p.get("address") == null || String.valueOf(p.get("address")).isBlank() ? "—" : p.get("address") %></td>
                    <td><span class="status-badge <%= visits > 0 ? "active" : "neutral" %>"><%= visits %> visit<%= visits == 1 ? "" : "s" %></span></td>
                    <td><%= lastVisit == null ? "No visits yet" : lastVisit %></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/deletePatient" method="post" style="display:inline"
                              onsubmit="return confirm('Delete patient <%= name.replace("'", "") %> (<%= p.get("patient_id") %>)? This permanently removes their appointment and billing history and cannot be undone.');">
                            <input type="hidden" name="patientId" value="<%= p.get("patient_id") %>">
                            <button class="btn btn-danger inline-btn" type="submit">
                                <i class="bi bi-trash3"></i> Delete
                            </button>
                        </form>
                    </td>
                </tr>
            <% }} else { %><tr><td colspan="8" style="text-align:center;color:#94a3b8;padding:28px">No patients found.</td></tr><% } %>
            </tbody>
        </table></div>
    </div>

    <div class="card">
        <div class="staff-toolbar">
            <div><h3><i class="bi bi-clipboard2-pulse"></i>&nbsp; Doctor &amp; Treatment History</h3><p>Every appointment on record, with the assigned doctor and treatment details.</p></div>
            <div class="table-search"><i class="bi bi-search"></i><input type="text" id="historySearch" placeholder="Search patient, doctor or treatment..." onkeyup="filterTable('historySearch','historyTable')" /></div>
        </div>
        <div class="table-wrap"><table class="data-table" id="historyTable">
            <thead><tr><th>Appointment</th><th>Patient</th><th>Doctor</th><th>Treatment</th><th>Fee</th><th>Date &amp; Time</th><th>Status</th></tr></thead>
            <tbody>
            <% if (historyList != null && !historyList.isEmpty()) { for (Map<String,Object> h : historyList) {
                String status = String.valueOf(h.get("status"));
                String statusClass = "Completed".equalsIgnoreCase(status) ? "active" : ("Cancelled".equalsIgnoreCase(status) ? "disabled" : "neutral");
            %>
                <tr>
                    <td><span class="id-chip"><%= h.get("appointment_number") %></span></td>
                    <td><strong><%= h.get("patient_name") %></strong><br><small style="color:#94a3b8"><%= h.get("patient_id") %></small></td>
                    <td><i class="bi bi-person-badge" style="color:var(--primary-dark);margin-right:5px"></i><%= h.get("dentist_name") %><br><small style="color:#94a3b8"><%= h.get("specialization") == null || String.valueOf(h.get("specialization")).isBlank() ? "General Dentistry" : h.get("specialization") %></small></td>
                    <td><%= h.get("treatment_name") %></td>
                    <td>LKR <%= String.format("%,.2f", ((Number) h.get("consultation_fee")).doubleValue()) %></td>
                    <td><%= h.get("appointment_date") %><br><small style="color:#94a3b8"><%= h.get("appointment_time") %></small></td>
                    <td><span class="status-badge <%= statusClass %>"><%= status %></span></td>
                </tr>
            <% }} else { %><tr><td colspan="7" style="text-align:center;color:#94a3b8;padding:28px">No appointment history yet.</td></tr><% } %>
            </tbody>
        </table></div>
    </div>
</div>
<script>
function filterTable(inputId, tableId) {
    var filter = document.getElementById(inputId).value.toLowerCase();
    var rows = document.getElementById(tableId).getElementsByTagName('tbody')[0].getElementsByTagName('tr');
    for (var i = 0; i < rows.length; i++) {
        rows[i].style.display = rows[i].textContent.toLowerCase().indexOf(filter) > -1 ? '' : 'none';
    }
}
</script>
</body>
</html>
