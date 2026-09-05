<%@ page import="com.SunriseDental.Model.Staff" %>
<%
    Staff currentDentist = (Staff) session.getAttribute("staff");
    String ctx = request.getContextPath();
    String currentUri = request.getRequestURI();
    String staffName = currentDentist == null ? "Dentist" : (currentDentist.getFullName() == null || currentDentist.getFullName().isBlank() ? currentDentist.getUsername() : currentDentist.getFullName());
    String initial = staffName.isBlank() ? "D" : staffName.substring(0,1).toUpperCase();
%>
<aside class="sidebar">
    <a class="sidebar-brand" href="<%= ctx %>/dentistDashboard">
        <span class="brand-mark" style="font-size:20px;">&#129463;</span>
        <span><strong>Sunrise Dental</strong><small>Dentist Portal</small></span>
    </a>

    <div class="sidebar-section">Workspace</div>
    <nav class="sidebar-nav">
        <a class="<%= currentUri.contains("dentistDashboard") ? "active" : "" %>" href="<%= ctx %>/dentistDashboard"><i class="bi bi-calendar2-week"></i><span>My Schedule</span></a>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-profile">
            <span class="profile-avatar"><%= initial %></span>
            <span class="profile-copy"><strong><%= staffName %></strong><small>Dentist</small></span>
        </div>
        <a class="sidebar-logout" href="<%= ctx %>/logout" title="Logout" onclick="return confirm('Are you sure you want to log out?');"><i class="bi bi-box-arrow-right"></i></a>
    </div>
</aside>

<header class="topbar">
    <div class="topbar-copy">
        <strong><i class="bi bi-calendar2-week" style="margin-right:8px;color:var(--primary)"></i>My Schedule</strong>
        <span>Sunrise Dental Clinic &middot; Your assigned appointments</span>
    </div>
    <div class="topbar-actions">
        <span class="system-status"><i class="bi bi-circle-fill"></i> System Online</span>
        <span class="role-badge solid-dentist topbar-role-badge">
            <i class="bi bi-clipboard2-pulse"></i>
            Dentist
        </span>
        <span class="topbar-avatar" title="<%= staffName %>"><%= initial %></span>
    </div>
</header>
