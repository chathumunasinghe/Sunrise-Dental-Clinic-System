<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create Account - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card" style="max-width:440px;">
            <div class="login-logo">
                <span class="icon-circle">&#129463;</span>
                <span class="brand-text">Sunrise Dental Clinic</span>
            </div>
            <h1>Create your patient account</h1>
            <div class="subtitle">Book appointments with our doctors &amp; pay online</div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/patientRegister" method="post" autocomplete="off">
                <div class="field">
                    <label>Full Name</label>
                    <input type="text" class="form-control" name="name" placeholder="Your full name"
                           value="<%= request.getAttribute("name") == null ? "" : request.getAttribute("name") %>" required autofocus />
                </div>
                <div class="form-row">
                    <div class="field">
                        <label>Contact Number</label>
                        <input type="text" class="form-control" name="contact" placeholder="07XXXXXXXX"
                               value="<%= request.getAttribute("contact") == null ? "" : request.getAttribute("contact") %>" />
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <input type="email" class="form-control" name="email" placeholder="you@example.com"
                               value="<%= request.getAttribute("email") == null ? "" : request.getAttribute("email") %>" />
                    </div>
                </div>
                <div class="field">
                    <label>Address</label>
                    <input type="text" class="form-control" name="address" placeholder="Your address"
                           value="<%= request.getAttribute("address") == null ? "" : request.getAttribute("address") %>" />
                </div>
                <div class="field">
                    <label>Choose a Username</label>
                    <input type="text" class="form-control" name="username" placeholder="username"
                           value="<%= request.getAttribute("username") == null ? "" : request.getAttribute("username") %>" required />
                </div>
                <div class="form-row">
                    <div class="field">
                        <label>Password</label>
                        <input type="password" class="form-control" name="password" placeholder="At least 6 characters" required pattern=".{6,}" />
                    </div>
                    <div class="field">
                        <label>Confirm Password</label>
                        <input type="password" class="form-control" name="confirmPassword" placeholder="Re-enter password" required pattern=".{6,}" />
                    </div>
                </div>
                <button type="submit" class="btn">Create Account</button>
            </form>

            <div class="auth-links" style="margin-top: 18px;">
                <a href="${pageContext.request.contextPath}/views/login.jsp?as=patient">
                    <i class="bi bi-arrow-left" style="font-size:14px;"></i>&nbsp;Back to Sign In
                </a>
            </div>
        </div>
    </div>
</body>
</html>
