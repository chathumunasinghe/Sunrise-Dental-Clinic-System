<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Help - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Help &amp; Guide</div>
        <div class="page-subtitle">How to use the staff system</div>

        <div class="card">
            <ol style="line-height: 2; font-size: 14px;">
                <li>Log in with your staff username and password.</li>
                <li>To register a new appointment, go to <strong>Register New Appointment</strong>, fill in the patient and appointment details, and submit. Leave "Patient ID" blank for a new patient.</li>
                <li>To view an existing appointment, go to <strong>Search Appointment</strong> and enter the appointment number.</li>
                <li>To generate and print a bill, go to <strong>Calculate &amp; Print Bill</strong> and enter the appointment number.</li>
                <li>Check <strong>Reports</strong> for daily appointment lists and revenue by treatment type.</li>
                <li>Admins can open <strong>Patients</strong> to see the full patient directory alongside every doctor and treatment record in one place.</li>
                <li>Admins can open <strong>Manage Staff</strong> to create accounts, enable/disable logins, or permanently delete an account.</li>
                <li>Click <strong>Logout</strong> to safely end your session when finished.</li>
            </ol>
        </div>
        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
