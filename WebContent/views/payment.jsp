<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Appointment, com.SunriseDental.Model.Bill" %>
<%
    Appointment appointment = (Appointment) request.getAttribute("appointment");
    Bill bill = (Bill) request.getAttribute("bill");
    boolean paymentSuccess = Boolean.TRUE.equals(request.getAttribute("paymentSuccess"));
    boolean alreadyPaid = bill != null && bill.isPaid();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Pay Online - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="page-title">Pay for Treatment</div>
        <div class="page-subtitle">Secure online payment for your appointment</div>

        <div class="card">
            <% if (bill == null || appointment == null) { %>
                <div class="alert alert-error">We couldn't find a bill for this appointment yet.</div>
            <% } else { %>
                <div class="pay-summary">
                    <div class="pay-row"><span>Appointment</span><span><%= appointment.getAppointmentNumber() %></span></div>
                    <div class="pay-row"><span>Date</span><span><%= appointment.getAppointmentDate() %> at <%= appointment.getAppointmentTime() %></span></div>
                    <div class="pay-row"><span>Status</span><span><%= appointment.getStatus() %></span></div>
                    <div style="margin-top:10px;">Amount Due</div>
                    <div class="pay-amount">LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></div>
                </div>

                <% if (paymentSuccess || alreadyPaid) { %>
                    <div class="alert alert-success">
                        <span class="paid-badge"><i class="bi bi-check-circle-fill"></i> Payment received &mdash; thank you!</span><br/>
                        Your appointment is confirmed. We look forward to seeing you.
                    </div>
                <% } else { %>
                    <form action="${pageContext.request.contextPath}/pay" method="post">
                        <input type="hidden" name="appointmentNumber" value="<%= appointment.getAppointmentNumber() %>" />
                        <div class="field">
                            <label>Name on Card</label>
                            <input type="text" class="form-control" placeholder="As shown on card" required />
                        </div>
                        <div class="card-fields">
                            <div class="field">
                                <label>Card Number</label>
                                <input type="text" class="form-control" placeholder="4111 1111 1111 1111" maxlength="19" required />
                            </div>
                            <div class="field">
                                <label>Expiry</label>
                                <input type="text" class="form-control" placeholder="MM/YY" maxlength="5" required />
                            </div>
                            <div class="field">
                                <label>CVV</label>
                                <input type="password" class="form-control" placeholder="123" maxlength="4" required />
                            </div>
                        </div>
                        <p style="font-size:11px;color:var(--text-muted);margin:-8px 0 16px;"><i class="bi bi-shield-check"></i> Demo checkout &mdash; no real card is charged.</p>
                        <button type="submit" class="btn"><i class="bi bi-credit-card"></i> Pay LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></button>
                    </form>
                <% } %>
            <% } %>
        </div>
        <a class="back-link" href="${pageContext.request.contextPath}/patientDashboard">&larr; Back to My Appointments</a>
    </div>
</body>
</html>
