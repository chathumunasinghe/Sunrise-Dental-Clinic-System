<%@ page import="com.SunriseDental.Model.Staff" %>
<%
    Staff currentStaff = (Staff) session.getAttribute("staff");
    boolean isAdminUser = currentStaff != null && currentStaff.isAdmin();
    String ctx = request.getContextPath();
    String currentUri = request.getRequestURI();
    String staffName = currentStaff == null ? "Staff" : (currentStaff.getFullName() == null || currentStaff.getFullName().isBlank() ? currentStaff.getUsername() : currentStaff.getFullName());
    String initial = staffName.isBlank() ? "S" : staffName.substring(0,1).toUpperCase();

    String pageTitle = "Sunrise Dental Clinic";
    String pageIcon = "bi-heart-pulse";
    if (currentUri.contains("dashboard")) { pageTitle = "Dashboard"; pageIcon = "bi-grid-1x2"; }
    else if (currentUri.contains("registerAppointment")) { pageTitle = "New Appointment"; pageIcon = "bi-calendar2-plus"; }
    else if (currentUri.contains("searchAppointment")) { pageTitle = "Appointments"; pageIcon = "bi-calendar2-check"; }
    else if (currentUri.contains("billing")) { pageTitle = "Billing"; pageIcon = "bi-receipt-cutoff"; }
    else if (currentUri.contains("reports")) { pageTitle = "Reports"; pageIcon = "bi-bar-chart"; }
    else if (currentUri.contains("managePatients")) { pageTitle = "Patients"; pageIcon = "bi-people"; }
    else if (currentUri.contains("manageStaff")) { pageTitle = "Manage Staff"; pageIcon = "bi-person-gear"; }
    else if (currentUri.contains("help")) { pageTitle = "Help &amp; Guide"; pageIcon = "bi-life-preserver"; }
%>
<aside class="sidebar">
    <a class="sidebar-brand" href="<%= ctx %>/dashboard">
        <span class="brand-mark"><i class="bi bi-heart-pulse"></i></span>
        <span><strong>Sunrise Dental</strong><small>Clinic Management</small></span>
    </a>

    <div class="sidebar-section">Workspace</div>
    <nav class="sidebar-nav">
        <a class="<%= currentUri.contains("dashboard") ? "active" : "" %>" href="<%= ctx %>/dashboard"><i class="bi bi-grid-1x2"></i><span>Dashboard</span></a>
        <a class="<%= currentUri.contains("registerAppointment") ? "active" : "" %>" href="<%= ctx %>/views/registerAppointment.jsp"><i class="bi bi-calendar2-plus"></i><span>New Appointment</span></a>
        <a class="<%= currentUri.contains("searchAppointment") ? "active" : "" %>" href="<%= ctx %>/views/searchAppointment.jsp"><i class="bi bi-calendar2-check"></i><span>Appointments</span></a>
        <a class="<%= currentUri.contains("billing") ? "active" : "" %>" href="<%= ctx %>/views/billing.jsp"><i class="bi bi-receipt-cutoff"></i><span>Billing</span></a>
        <% if (isAdminUser) { %>
        <a class="<%= currentUri.contains("reports") ? "active" : "" %>" href="<%= ctx %>/reports"><i class="bi bi-bar-chart"></i><span>Reports</span></a>
        <a class="<%= currentUri.contains("managePatients") ? "active" : "" %>" href="<%= ctx %>/managePatients"><i class="bi bi-people"></i><span>Patients</span></a>
        <a class="<%= currentUri.contains("manageStaff") ? "active" : "" %>" href="<%= ctx %>/manageStaff"><i class="bi bi-person-gear"></i><span>Manage Staff</span></a>
        <% } %>
    </nav>

    <div class="sidebar-section">Support</div>
    <nav class="sidebar-nav">
        <a class="<%= currentUri.contains("help") ? "active" : "" %>" href="<%= ctx %>/views/help.jsp"><i class="bi bi-life-preserver"></i><span>Help &amp; Guide</span></a>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-profile">
            <span class="profile-avatar"><%= initial %></span>
            <span class="profile-copy"><strong><%= staffName %></strong><small><%= currentStaff == null ? "" : currentStaff.getRole() %></small></span>
        </div>
        <a class="sidebar-logout" href="<%= ctx %>/logout" title="Logout"><i class="bi bi-box-arrow-right"></i></a>
    </div>
</aside>

<header class="topbar">
    <div class="topbar-copy">
        <strong><i class="bi <%= pageIcon %>" style="margin-right:8px;color:var(--primary)"></i><%= pageTitle %></strong>
        <span>Sunrise Dental Clinic &middot; Appointment &amp; Billing System</span>
    </div>
    <div class="topbar-actions">
        <span class="system-status"><i class="bi bi-circle-fill"></i> System Online</span>
        <span class="role-badge <%= isAdminUser ? "solid-admin" : "solid-guest" %> topbar-role-badge">
            <i class="bi <%= isAdminUser ? "bi-shield-lock" : "bi-person-check" %>"></i>
            <%= isAdminUser ? "Administrator" : "Front Desk" %>
        </span>
        <span class="topbar-avatar" title="<%= staffName %>"><%= initial %></span>
    </div>
</header>
