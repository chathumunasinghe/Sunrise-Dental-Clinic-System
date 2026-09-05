<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Patient, java.util.List, java.util.Map" %>
<%
    Patient loggedInPatient = (Patient) session.getAttribute("patient");
    if (loggedInPatient == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> myAppointments = (List<Map<String,Object>>) request.getAttribute("myAppointments");
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> notifications = (List<Map<String,Object>>) request.getAttribute("notifications");
    int total = myAppointments == null ? 0 : myAppointments.size();
    int upcoming = 0;
    if (myAppointments != null) {
        for (Map<String,Object> a : myAppointments) {
            if ("Scheduled".equalsIgnoreCase(String.valueOf(a.get("status")))) upcoming++;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Appointments - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="dashboard-heading">
            <div>
                <h1>Hello, <%= loggedInPatient.getName() %></h1>
                <p>Here's an overview of your appointments with us.</p>
            </div>
            <a class="btn" style="width:auto;padding:11px 20px;" href="${pageContext.request.contextPath}/doctors"><i class="bi bi-calendar-plus"></i> Book New Appointment</a>
        </div>

        <div class="stat-grid" style="grid-template-columns:repeat(2,minmax(0,1fr));">
            <div class="stat-card"><div class="stat-copy"><small>Total Appointments</small><strong><%= total %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-check"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Upcoming</small><strong><%= upcoming %></strong></div><span class="stat-icon"><i class="bi bi-clock-history"></i></span></div>
        </div>

        <div class="care-banner" style="background:linear-gradient(135deg, var(--primary), var(--primary-dark)); color:#fff; border-radius:16px; padding:18px 22px; margin-bottom:20px; display:flex; align-items:center; justify-content:space-between; gap:14px; flex-wrap:wrap;">
            <div style="display:flex; align-items:center; gap:14px;">
                <i class="bi bi-shield-check" style="font-size:26px;"></i>
                <div>
                    <div style="font-weight:700; font-size:14.5px;">Getting treatment soon?</div>
                    <div style="font-size:12.5px; opacity:.9;">Read the precautions to follow before &amp; after your visit.</div>
                </div>
            </div>
            <a class="btn" style="width:auto; padding:9px 18px; background:#fff; color:var(--primary-dark);" href="${pageContext.request.contextPath}/views/carePrecautions.jsp"><i class="bi bi-arrow-right-circle"></i> View Care Instructions</a>
        </div>

        <div class="card" id="notifications">
            <h3 style="margin-top:0;"><i class="bi bi-bell"></i> Notifications</h3>
            <% if (notifications == null || notifications.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-bell-slash"></i>No notifications yet.</div>
            <% } else { %>
                <div style="display:flex; flex-direction:column; gap:8px;">
                <% for (Map<String,Object> n : notifications) { %>
                    <div style="display:flex; align-items:flex-start; gap:10px; padding:10px 12px; background:var(--primary-light); border-radius:10px;">
                        <i class="bi bi-info-circle-fill" style="color:var(--primary); margin-top:2px;"></i>
                        <div>
                            <div style="font-size:13.5px;"><%= n.get("message") %></div>
                            <div style="font-size:11px; color:var(--text-muted); margin-top:2px;"><%= n.get("created_at") %></div>
                        </div>
                    </div>
                <% } %>
                </div>
            <% } %>
        </div>

        <div class="card">
            <h3 style="margin-top:0;">My Appointments</h3>
            <% if (myAppointments == null || myAppointments.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-calendar2"></i>You haven't booked an appointment yet.</div>
            <% } else { %>
            <div class="table-wrap"><table class="data-table">
                <thead><tr><th>Appointment</th><th>Doctor</th><th>Treatment</th><th>Date &amp; Time</th><th>Status</th><th>Doctor's Notes</th><th>Payment</th><th>Care</th></tr></thead>
                <tbody>
                <% for (Map<String,Object> a : myAppointments) {
                    String status = String.valueOf(a.get("status"));
                    String statusClass = "Completed".equalsIgnoreCase(status) ? "active" : ("Cancelled".equalsIgnoreCase(status) ? "disabled" : "neutral");
                    boolean hasBill = Boolean.TRUE.equals(a.get("has_bill"));
                    boolean paid = Boolean.TRUE.equals(a.get("paid"));
                %>
                    <tr>
                        <td><span class="id-chip"><%= a.get("appointment_number") %></span></td>
                        <td><%= a.get("dentist_name") %><br><small style="color:#94a3b8;"><%= a.get("specialization") == null || String.valueOf(a.get("specialization")).isBlank() ? "General Dentistry" : a.get("specialization") %></small></td>
                        <td><%= a.get("treatment_name") %><br><small style="color:#94a3b8;">LKR <%= String.format("%,.2f", ((Number) a.get("consultation_fee")).doubleValue()) %></small></td>
                        <td><%= a.get("appointment_date") %><br><small style="color:#94a3b8;"><%= a.get("appointment_time") %></small></td>
                        <td><span class="status-badge <%= statusClass %>"><%= status %></span></td>
                        <td style="max-width:220px;">
                            <% String notes = (String) a.get("treatment_notes");
                               if (notes == null || notes.isBlank()) { %>
                                <span style="color:var(--text-muted); font-size:12px;">&mdash;</span>
                            <% } else { %>
                                <div style="font-size:12.5px; white-space:pre-wrap;"><i class="bi bi-file-earmark-medical" style="color:var(--primary); margin-right:4px;"></i><%= notes %></div>
                            <% } %>
                        </td>
                        <td>
                            <% if (!hasBill) { %>
                                <span style="color:var(--text-muted); font-size:12px;">&mdash;</span>
                            <% } else if (paid) { %>
                                <span class="paid-badge"><i class="bi bi-check-circle-fill"></i> Paid</span>
                                <a class="back-link" style="margin-left:8px; font-size:12px;" href="${pageContext.request.contextPath}/myReceipt?appointmentNumber=<%= a.get("appointment_number") %>"><i class="bi bi-receipt"></i> Receipt</a>
                            <% } else { %>
                                <a class="btn inline-btn" href="${pageContext.request.contextPath}/pay?appointmentNumber=<%= a.get("appointment_number") %>"><i class="bi bi-credit-card"></i> Pay Now</a>
                                <a class="back-link" style="margin-left:8px; font-size:12px;" href="${pageContext.request.contextPath}/myReceipt?appointmentNumber=<%= a.get("appointment_number") %>"><i class="bi bi-receipt"></i> Receipt</a>
                            <% } %>
                        </td>
                        <td>
                            <a class="btn btn-secondary inline-btn" style="text-decoration:none;"
                               href="${pageContext.request.contextPath}/views/carePrecautions.jsp?treatment=<%= java.net.URLEncoder.encode(String.valueOf(a.get("treatment_name")), "UTF-8") %>">
                                <i class="bi bi-shield-check"></i> Care Tips
                            </a>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table></div>
            <% } %>
        </div>

        <div class="card" style="border:1px solid #fecaca;">
            <h3 style="margin-top:0; color:var(--danger);"><i class="bi bi-exclamation-triangle"></i> Delete My Account</h3>
            <p style="color:var(--text-muted); font-size:13px;">
                This permanently deletes your portal account, including every appointment and billing record tied to it. This cannot be undone.
            </p>
            <% if (request.getAttribute("accountDeleteError") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("accountDeleteError") %></div>
            <% } %>
            <form action="${pageContext.request.contextPath}/deleteMyAccount" method="post"
                  onsubmit="return confirm('This will permanently delete your account and all appointment history. Continue?');"
                  style="display:flex; gap:12px; align-items:flex-end; flex-wrap:wrap;">
                <div class="field" style="flex:1; min-width:220px; margin-bottom:0;">
                    <label>Confirm your password</label>
                    <input type="password" class="form-control" name="currentPassword" placeholder="Current password" required />
                </div>
                <button type="submit" class="btn btn-danger" style="width:auto; padding:11px 20px;">
                    <i class="bi bi-trash3"></i> Delete My Account
                </button>
            </form>
        </div>
    </div>
</body>
</html>
