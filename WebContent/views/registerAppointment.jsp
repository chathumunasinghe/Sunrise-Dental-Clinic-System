<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Register New Appointment</div>
        <div class="page-subtitle">Enter patient and appointment details below</div>

        <div class="card">
            <% if (request.getAttribute("message") != null) { %>
                <div class="alert alert-success"><%= request.getAttribute("message") %></div>
            <% } %>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/registerAppointment" method="post">
                <div class="form-row">
                    <div class="field">
                        <label>Existing Patient ID (optional)</label>
                        <input type="text" class="form-control" name="patientId" placeholder="e.g. PT0001" />
                    </div>
                    <div class="field">
                        <label>Patient Name</label>
                        <input type="text" class="form-control" name="name" placeholder="Full name" required />
                    </div>
                </div>

                <div class="form-row">
                    <div class="field">
                        <label>Address</label>
                        <input type="text" class="form-control" name="address" placeholder="Patient address" />
                    </div>
                    <div class="field">
                        <label>Contact Number</label>
                        <input type="text" class="form-control" name="contact" placeholder="07XXXXXXXX" required pattern="[0-9]{10}" />
                    </div>
                </div>

                <div class="field">
                    <label>Email (for confirmation)</label>
                    <input type="email" class="form-control" name="email" placeholder="patient@example.com" />
                </div>

                <div class="form-row">
                    <div class="field">
                        <label>Dentist</label>
                        <select name="dentistId" class="form-select" required>
                            <option value="1">Dr. Perera - General Dentistry</option>
                            <option value="2">Dr. Silva - Orthodontics</option>
                        </select>
                    </div>
                    <div class="field">
                        <label>Treatment Type</label>
                        <select name="treatmentId" class="form-select" required>
                            <option value="1">Consultation</option>
                            <option value="2">Tooth Filling</option>
                            <option value="3">Tooth Extraction</option>
                            <option value="4">Root Canal</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="field">
                        <label>Appointment Date</label>
                        <input type="date" class="form-control" name="date" required />
                    </div>
                    <div class="field">
                        <label>Appointment Time</label>
                        <input type="time" class="form-control" name="time" required />
                    </div>
                </div>

                <button type="submit" class="btn">Register Appointment</button>
            </form>
        </div>
        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
