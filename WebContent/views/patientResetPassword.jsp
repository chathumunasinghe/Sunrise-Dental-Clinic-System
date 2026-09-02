<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reset Password - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="login-logo">
                <span class="icon-circle"><i class="bi bi-shield-lock"></i></span>
                <span class="brand-text">Sunrise Dental Clinic</span>
            </div>
            <h1>Reset your password</h1>
            <div class="subtitle">Patient portal &middot; enter and confirm your new password</div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <% if (request.getAttribute("token") != null) { %>
                <form action="${pageContext.request.contextPath}/patientResetPassword" method="post">
                    <input type="hidden" name="token" value="<%= request.getAttribute("token") %>" />
                    <div class="field-icon">
                        <label>New Password</label>
                        <i class="bi bi-lock"></i>
                        <input type="password" name="newPassword" placeholder="At least 6 characters" required pattern=".{6,}" title="At least 6 characters" autofocus />
                    </div>
                    <div class="field-icon">
                        <label>Confirm New Password</label>
                        <i class="bi bi-lock-fill"></i>
                        <input type="password" name="confirmPassword" placeholder="Re-enter new password" required pattern=".{6,}" title="Must match the new password above" />
                    </div>
                    <button type="submit" class="btn" style="margin-top: 6px;">Update Password</button>
                </form>
            <% } else { %>
                <a class="btn" href="${pageContext.request.contextPath}/patientForgotPassword" style="display:block; text-align:center;">Request a New Link</a>
            <% } %>

            <div class="auth-links" style="margin-top: 18px;">
                <a href="${pageContext.request.contextPath}/views/login.jsp?as=patient">
                    <i class="bi bi-arrow-left" style="font-size:14px;"></i>
                    &nbsp;Back to Sign In
                </a>
            </div>
        </div>
    </div>
</body>
</html>
