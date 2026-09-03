<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Staff, java.util.List, java.util.Map" %>
<%
    Staff loggedInDentist = (Staff) session.getAttribute("staff");
    if (loggedInDentist == null || !loggedInDentist.isDentist()) {
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        return;
    }
    @SuppressWarnings("unchecked")
    List<Map<String,Object>> myAppointments = (List<Map<String,Object>>) request.getAttribute("myAppointments");
    int total = myAppointments == null ? 0 : myAppointments.size();
    int scheduledCount = 0, completedCount = 0;
    if (myAppointments != null) {
        for (Map<String,Object> a : myAppointments) {
            String st = String.valueOf(a.get("status"));
            if ("Scheduled".equalsIgnoreCase(st)) scheduledCount++;
            if ("Completed".equalsIgnoreCase(st)) completedCount++;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Schedule - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <style>
        .note-details { margin-top: 8px; }
        .note-details summary {
            cursor: pointer; font-size: 12.5px; color: var(--primary-dark);
            font-weight: 600; list-style: none;
        }
        .note-details summary::-webkit-details-marker { display: none; }
        .note-details summary::before { content: "\f4fc"; font-family: "bootstrap-icons"; margin-right: 6px; }
        .note-form { margin-top: 10px; background: var(--primary-light); border-radius: 10px; padding: 14px; }
        .note-form textarea {
            width: 100%; min-height: 70px; border: 1.5px solid #d7dee0; border-radius: 8px;
            padding: 8px 10px; font-size: 13px; font-family: inherit; resize: vertical;
        }
        .note-form-actions { display: flex; align-items: center; justify-content: space-between; margin-top: 8px; }
        .note-form-actions label { font-size: 12px; color: var(--text-muted); display: flex; align-items: center; gap: 6px; }
    </style>
</head>
<body>
    <%@ include file="includes/dentistTopbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">My Schedule</div>
        <div class="page-subtitle">Appointments assigned to you &middot; <span class="role-badge solid-dentist">Dentist</span></div>

        <div class="stat-grid" style="grid-template-columns:repeat(3,minmax(0,1fr)); margin-bottom:20px;">
            <div class="stat-card"><div class="stat-copy"><small>Total Assigned</small><strong><%= total %></strong></div><span class="stat-icon"><i class="bi bi-calendar2-week"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Scheduled</small><strong><%= scheduledCount %></strong></div><span class="stat-icon"><i class="bi bi-clock-history"></i></span></div>
            <div class="stat-card"><div class="stat-copy"><small>Completed</small><strong><%= completedCount %></strong></div><span class="stat-icon"><i class="bi bi-check-circle"></i></span></div>
        </div>

        <div class="card">
            <h3 style="margin-top:0;">Assigned Appointments</h3>
            <% if (myAppointments == null || myAppointments.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-calendar2"></i>No appointments assigned to you yet.</div>
            <% } else { %>
                <div class="table-wrap">
                <table class="data-table">
                    <thead><tr><th>Appointment</th><th>Patient</th><th>Treatment</th><th>Date &amp; Time</th><th>Status</th></tr></thead>
                    <tbody>
                    <% for (Map<String,Object> a : myAppointments) {
                        String status = String.valueOf(a.get("status"));
                        String statusClass = "Completed".equalsIgnoreCase(status) ? "active" : ("Cancelled".equalsIgnoreCase(status) ? "disabled" : "neutral");
                        String existingNotes = a.get("treatment_notes") == null ? "" : String.valueOf(a.get("treatment_notes"));
                    %>
                        <tr>
                            <td colspan="5" style="padding:0; border-bottom:none;">
                                <div style="padding:14px 12px; border-bottom:1px solid #f0f2f4;">
                                    <div style="display:grid; grid-template-columns:1fr 1.4fr 1.2fr 1.2fr 1fr; gap:12px; align-items:start; font-size:13.5px;">
                                        <div><span class="id-chip"><%= a.get("appointment_number") %></span></div>
                                        <div>
                                            <strong><%= a.get("patient_name") %></strong><br/>
                                            <small style="color:#94a3b8;"><%= a.get("contact_number") %></small>
                                        </div>
                                        <div><%= a.get("treatment_name") %></div>
                                        <div><%= a.get("appointment_date") %><br/><small style="color:#94a3b8;"><%= a.get("appointment_time") %></small></div>
                                        <div><span class="status-badge <%= statusClass %>"><%= status %></span></div>
                                    </div>

                                    <details class="note-details">
                                        <summary><%= existingNotes.isBlank() ? "Add treatment notes" : "View / edit treatment notes" %></summary>
                                        <form class="note-form" action="${pageContext.request.contextPath}/updateTreatmentNotes" method="post">
                                            <input type="hidden" name="appointmentNumber" value="<%= a.get("appointment_number") %>" />
                                            <textarea name="notes" placeholder="e.g. Patient presented with mild sensitivity on upper molar. Recommended fluoride treatment..."><%= existingNotes %></textarea>
                                            <div class="note-form-actions">
                                                <label>
                                                    <input type="checkbox" name="markCompleted" value="1" <%= "Completed".equalsIgnoreCase(status) ? "checked disabled" : "" %> />
                                                    Mark this visit as Completed
                                                </label>
                                                <button type="submit" class="btn" style="width:auto; padding:8px 18px; font-size:12.5px;">Save Notes</button>
                                            </div>
                                        </form>
                                    </details>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
