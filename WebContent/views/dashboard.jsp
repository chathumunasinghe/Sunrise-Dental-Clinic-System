<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Staff, java.util.List, java.util.Map, java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%
    Staff loggedInStaff = (Staff) session.getAttribute("staff");
    if (loggedInStaff == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    boolean admin = loggedInStaff.isAdmin();
    Integer todayAppointments = (Integer) request.getAttribute("todayAppointments");
    Integer totalPatients = (Integer) request.getAttribute("totalPatients");
    Integer activeStaff = (Integer) request.getAttribute("activeStaff");
    Double todayRevenue = (Double) request.getAttribute("todayRevenue");
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> todaySchedule = (List<Map<String,Object>>) request.getAttribute("todaySchedule");
    if (todayAppointments == null) todayAppointments = 0;
    if (totalPatients == null) totalPatients = 0;
    if (activeStaff == null) activeStaff = 0;
    if (todayRevenue == null) todayRevenue = 0.0;
    String displayName = loggedInStaff.getFullName() == null || loggedInStaff.getFullName().isBlank() ? loggedInStaff.getUsername() : loggedInStaff.getFullName();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
<%@ include file="includes/topbar.jsp" %>
<div class="page-wrapper">
    <div class="dashboard-heading">
        <div>
            <h1>Good day, <%= displayName %></h1>
            <p>Here is today's clinic activity and the tasks that need your attention.</p>
        </div>
        <div class="dashboard-date"><i class="bi bi-calendar3"></i>&nbsp; <%= LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")) %></div>
    </div>

    <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><%= request.getAttribute("error") %></div><% } %>

    <div class="stat-grid">
        <div class="stat-card"><div class="stat-copy"><small>Today's Appointments</small><strong><%= todayAppointments %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-check"></i></span></div>
        <div class="stat-card"><div class="stat-copy"><small>Total Patients</small><strong><%= totalPatients %></strong></div><span class="stat-icon"><i class="bi bi-person-heart"></i></span></div>
        <div class="stat-card"><div class="stat-copy"><small>Today's Revenue</small><strong style="font-size:19px;">LKR <%= String.format("%,.0f", todayRevenue) %></strong></div><span class="stat-icon"><i class="bi bi-cash-stack"></i></span></div>
        <div class="stat-card"><div class="stat-copy"><small>Active Staff</small><strong><%= activeStaff %></strong></div><span class="stat-icon"><i class="bi bi-people"></i></span></div>
    </div>

    <div class="dashboard-grid">
        <section class="panel">
            <div class="panel-header"><h2><i class="bi bi-clock-history"></i>&nbsp; Today's Schedule</h2><a href="${pageContext.request.contextPath}/views/searchAppointment.jsp">Find appointment <i class="bi bi-arrow-right"></i></a></div>
            <div class="panel-body" style="padding-top:8px;">
                <% if (todaySchedule == null || todaySchedule.isEmpty()) { %>
                    <div class="empty-state"><i class="bi bi-calendar2"></i>No appointments are scheduled for today yet.</div>
                <% } else { %>
                <div class="table-wrap"><table class="schedule-table">
                    <thead><tr><th>Time</th><th>Patient</th><th>Dentist</th><th>Treatment</th><th>Status</th></tr></thead>
                    <tbody>
                    <% for (Map<String,Object> row : todaySchedule) { %>
                        <tr>
                            <td><strong><%= row.get("appointment_time") %></strong></td>
                            <td><%= row.get("patient_name") %><br><small style="color:#94a3b8"><%= row.get("appointment_number") %></small></td>
                            <td><%= row.get("dentist_name") %></td>
                            <td><%= row.get("treatment_name") %></td>
                            <td><span class="status-badge active"><%= row.get("status") %></span></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table></div>
                <% } %>
            </div>
        </section>

        <aside class="panel">
            <div class="panel-header"><h2><i class="bi bi-lightning-charge"></i>&nbsp; Quick Actions</h2><span><%= loggedInStaff.getRole() %> access</span></div>
            <div class="panel-body">
                <div class="quick-actions">
                    <a class="quick-action" href="${pageContext.request.contextPath}/views/registerAppointment.jsp"><i class="bi bi-calendar-plus"></i><span><strong>New Appointment</strong><small>Register a patient visit</small></span></a>
                    <a class="quick-action" href="${pageContext.request.contextPath}/views/searchAppointment.jsp"><i class="bi bi-search"></i><span><strong>Search</strong><small>Find appointment details</small></span></a>
                    <a class="quick-action" href="${pageContext.request.contextPath}/views/billing.jsp"><i class="bi bi-receipt"></i><span><strong>Billing</strong><small>Create a patient bill</small></span></a>
                    <% if (admin) { %>
                    <a class="quick-action" href="${pageContext.request.contextPath}/reports"><i class="bi bi-bar-chart-line"></i><span><strong>Reports</strong><small>Review clinic performance</small></span></a>
                    <a class="quick-action" href="${pageContext.request.contextPath}/managePatients"><i class="bi bi-people"></i><span><strong>Patients</strong><small>View patient &amp; treatment records</small></span></a>
                    <a class="quick-action" href="${pageContext.request.contextPath}/manageStaff"><i class="bi bi-person-gear"></i><span><strong>Staff</strong><small>Manage system accounts</small></span></a>
                    <% } %>
                    <a class="quick-action" href="${pageContext.request.contextPath}/views/help.jsp"><i class="bi bi-question-circle"></i><span><strong>Help</strong><small>System usage guide</small></span></a>
                </div>
            </div>
        </aside>
    </div>
</div>
</body>
</html>
