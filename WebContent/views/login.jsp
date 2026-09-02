<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String activeTab = request.getParameter("as");
    if (activeTab == null) activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null) activeTab = "patient";
    boolean patientTab = "patient".equals(activeTab);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign In - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="login-logo">
                <span class="icon-circle">&#129463;</span>
                <span class="brand-text">Sunrise Dental Clinic</span>
            </div>

            <div class="role-toggle">
                <button type="button" class="role-toggle-btn <%= patientTab ? "active" : "" %>" data-tab="patient" onclick="showTab('patient')">
                    <i class="bi bi-person"></i> I'm a Patient
                </button>
                <button type="button" class="role-toggle-btn <%= !patientTab ? "active" : "" %>" data-tab="staff" onclick="showTab('staff')">
                    <i class="bi bi-shield-lock"></i> Staff / Front Desk
                </button>
            </div>

            <% if ("success".equals(request.getParameter("reset"))) { %>
                <div class="alert alert-success">Your password has been reset. Please sign in.</div>
            <% } %>
            <% if ("success".equals(request.getParameter("signup"))) { %>
                <div class="alert alert-success">Account created! Please sign in below.</div>
            <% } %>

            <!-- ===================== Patient sign-in ===================== -->
            <div id="tab-patient" class="auth-tab" style="display:<%= patientTab ? "block" : "none" %>;">
                <h1>Patient Portal</h1>
                <div class="subtitle">Book appointments, meet our doctors &amp; pay online</div>

                <% if (request.getAttribute("patientError") != null) { %>
                    <div class="alert alert-error"><%= request.getAttribute("patientError") %></div>
                <% } %>

                <form action="${pageContext.request.contextPath}/patientLogin" method="post" autocomplete="off">
                    <div class="field-icon">
                        <label>Username</label>
                        <i class="bi bi-person"></i>
                        <input type="text" name="patientUsername" placeholder="your username"
                               value="<%= request.getAttribute("patientUsername") == null ? "" : request.getAttribute("patientUsername") %>"
                               required <%= patientTab ? "autofocus" : "" %> />
                    </div>
                    <div class="field-icon">
                        <label>Password</label>
                        <i class="bi bi-lock"></i>
                        <input type="password" name="patientPassword" placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" required />
                    </div>
                    <div class="remember-row">
                        <span></span>
                        <a href="${pageContext.request.contextPath}/patientForgotPassword" style="color: var(--primary-dark); font-weight:600; text-decoration:none;">Forgot password?</a>
                    </div>
                    <button type="submit" class="btn">Sign In</button>
                </form>
                <div class="auth-links" style="margin-top: 16px;">
                    New patient? <a href="${pageContext.request.contextPath}/views/patientSignup.jsp">&nbsp;Create an account</a>
                </div>
            </div>

            <!-- ===================== Staff sign-in ===================== -->
            <div id="tab-staff" class="auth-tab" style="display:<%= !patientTab ? "block" : "none" %>;">
                <h1>Staff Portal Access</h1>
                <div class="subtitle">Sign in to manage appointments &amp; billing</div>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-error"><%= request.getAttribute("error") %></div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="off">
                    <div class="field-icon">
                        <label>Username</label>
                        <i class="bi bi-person"></i>
                        <input type="text" name="username" placeholder="admin"
                               value="<%= request.getAttribute("username") == null ? "" : request.getAttribute("username") %>"
                               required <%= !patientTab ? "autofocus" : "" %> />
                    </div>
                    <div class="field-icon">
                        <label>Password</label>
                        <i class="bi bi-lock"></i>
                        <input type="password" name="password" placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" required />
                    </div>

                    <div class="remember-row">
                        <label><input type="checkbox" name="rememberMe" /> Remember me</label>
                        <a href="${pageContext.request.contextPath}/forgotPassword" style="color: var(--primary-dark); font-weight:600; text-decoration:none;">Forgot password?</a>
                    </div>

                    <button type="submit" class="btn">Sign In</button>
                </form>
            </div>
        </div>
    </div>
    <script>
        function showTab(tab) {
            document.getElementById('tab-patient').style.display = (tab === 'patient') ? 'block' : 'none';
            document.getElementById('tab-staff').style.display = (tab === 'staff') ? 'block' : 'none';
            document.querySelectorAll('.role-toggle-btn').forEach(function (btn) {
                btn.classList.toggle('active', btn.getAttribute('data-tab') === tab);
            });
        }
    </script>
</body>
</html>
