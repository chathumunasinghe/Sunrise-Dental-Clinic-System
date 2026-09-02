<%@ page contentType="text/html;charset=UTF-8" %>
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
                <span class="icon-circle">&#129468;</span>
                <span class="brand-text">Sunrise Dental Clinic</span>
            </div>
            <h1>Staff Portal Access</h1>
            <div class="subtitle">Sign in to manage appointments &amp; billing</div>

            <% if ("success".equals(request.getParameter("reset"))) { %>
                <div class="alert alert-success">Your password has been reset. Please sign in.</div>
            <% } %>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="off">
                <div class="field-icon">
                    <label>Username</label>
                    <i class="bi bi-person"></i>
                    <input type="text" name="username" placeholder="admin"
                           value="<%= request.getAttribute("username") == null ? "" : request.getAttribute("username") %>"
                           required autofocus />
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
</body>
</html>
