<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Forgot Password - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="login-logo">
                <span class="icon-circle"><i class="bi bi-key"></i></span>
                <span class="brand-text">Sunrise Dental Clinic</span>
            </div>
            <h1>Forgot your password?</h1>
            <div class="subtitle">Patient portal &middot; we'll email you a link to reset it</div>

            <% if (request.getAttribute("message") != null) { %>
                <div class="alert alert-success"><%= request.getAttribute("message") %></div>
            <% } %>

            <% if (request.getAttribute("resetLink") != null) { %>
                <div class="alert alert-success" style="word-break: break-all; text-align:left;">
                    <strong>Demo mode:</strong> since no SMTP server is configured, here is the link
                    that would normally be emailed:<br/>
                    <a href="<%= request.getAttribute("resetLink") %>"><%= request.getAttribute("resetLink") %></a>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/patientForgotPassword" method="post">
                <div class="field-icon">
                    <label>Username or Email</label>
                    <i class="bi bi-envelope"></i>
                    <input type="text" name="identifier" placeholder="your username or email" required autofocus />
                </div>
                <button type="submit" class="btn" style="margin-top: 6px;">Send Reset Link</button>
            </form>

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
