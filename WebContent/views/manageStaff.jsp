<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Staff, java.util.List" %>
<%
    Staff loggedInStaff = (Staff) session.getAttribute("staff");
    if (loggedInStaff == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    if (!loggedInStaff.isAdmin()) { response.sendRedirect(request.getContextPath() + "/dashboard"); return; }
    @SuppressWarnings("unchecked") List<Staff> staffList = (List<Staff>) request.getAttribute("staffList");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Staff - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
<%@ include file="includes/topbar.jsp" %>
<div class="page-wrapper">
    <div class="page-title">Manage Staff</div>
    <div class="page-subtitle">Create staff accounts and control access to the clinic management system.</div>

    <% if (request.getAttribute("message") != null) { %><div class="alert alert-success"><%= request.getAttribute("message") %></div><% } %>
    <% if (request.getAttribute("error") != null) { %><div class="alert alert-error"><%= request.getAttribute("error") %></div><% } %>

    <div class="card">
        <div class="staff-toolbar"><div><h3><i class="bi bi-person-plus"></i>&nbsp; Add Staff Account</h3><p>Create an administrator or front-desk user.</p></div><span class="role-badge solid-admin"><i class="bi bi-shield-lock"></i> Admin only</span></div>
        <form action="${pageContext.request.contextPath}/manageStaff" method="post">
            <input type="hidden" name="action" value="add" />
            <div class="form-row">
                <div class="field"><label>Full Name</label><input type="text" class="form-control" name="fullName" placeholder="Staff member's full name" required maxlength="100" /></div>
                <div class="field"><label>Username</label><input type="text" class="form-control" name="username" placeholder="Unique login username" required maxlength="50" pattern="[A-Za-z0-9._-]{3,50}" /></div>
            </div>
            <div class="form-row">
                <div class="field"><label>Email Address</label><input type="email" class="form-control" name="email" placeholder="name@sunrisedental.lk" required maxlength="100" /></div>
                <div class="field"><label>Temporary Password</label><input type="password" class="form-control" name="password" placeholder="Minimum 6 characters" required pattern=".{6,}" title="Minimum 6 characters" /></div>
            </div>
            <div class="field" style="max-width:280px"><label>System Role</label><select name="role" class="form-select" required><option value="GUEST">Guest / Front Desk</option><option value="ADMIN">Administrator</option></select></div>
            <button type="submit" class="btn inline-btn" style="padding:10px 18px"><i class="bi bi-plus-circle"></i>Create Account</button>
        </form>
    </div>

    <div class="card">
        <div class="staff-toolbar"><div><h3>Staff Directory</h3><p><%= staffList == null ? 0 : staffList.size() %> account(s) registered in the system.</p></div></div>
        <div class="table-wrap"><table class="data-table">
            <thead><tr><th>Staff Member</th><th>Username</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <% if (staffList != null && !staffList.isEmpty()) { for (Staff s : staffList) {
                String name = s.getFullName() == null || s.getFullName().isBlank() ? s.getUsername() : s.getFullName();
                String initial2 = name.substring(0,1).toUpperCase(); %>
                <tr>
                    <td><span class="staff-avatar"><%= initial2 %></span><strong><%= name %></strong></td>
                    <td><%= s.getUsername() %></td><td><%= s.getEmail() == null ? "—" : s.getEmail() %></td>
                    <td><span class="role-badge <%= s.isAdmin() ? "solid-admin" : "solid-guest" %>"><%= s.getRole() %></span></td>
                    <td><span class="status-badge <%= "ACTIVE".equals(s.getStatus()) ? "active" : "disabled" %>"><%= s.getStatus() %></span></td>
                    <td>
                    <% if (s.getStaffId() != loggedInStaff.getStaffId()) { %>
                        <div style="display:flex;gap:8px;flex-wrap:wrap">
                            <form action="${pageContext.request.contextPath}/manageStaff" method="post" style="display:inline">
                                <input type="hidden" name="action" value="toggleStatus"><input type="hidden" name="staffId" value="<%= s.getStaffId() %>">
                                <input type="hidden" name="newStatus" value="<%= "ACTIVE".equals(s.getStatus()) ? "DISABLED" : "ACTIVE" %>">
                                <button class="btn btn-secondary inline-btn" type="submit"><i class="bi <%= "ACTIVE".equals(s.getStatus()) ? "bi-person-x" : "bi-person-check" %>"></i><%= "ACTIVE".equals(s.getStatus()) ? "Disable" : "Enable" %></button>
                            </form>
                            <form action="${pageContext.request.contextPath}/manageStaff" method="post" style="display:inline" onsubmit="return confirm('Permanently delete this staff account? This cannot be undone.');">
                                <input type="hidden" name="action" value="delete"><input type="hidden" name="staffId" value="<%= s.getStaffId() %>">
                                <button class="btn btn-danger inline-btn" type="submit"><i class="bi bi-trash3"></i>Delete</button>
                            </form>
                        </div>
                    <% } else { %><span style="font-size:11px;color:#94a3b8">Current user</span><% } %>
                    </td>
                </tr>
            <% }} else { %><tr><td colspan="6" style="text-align:center;color:#94a3b8;padding:28px">No staff accounts found.</td></tr><% } %>
            </tbody>
        </table></div>
    </div>
</div>
</body>
</html>
