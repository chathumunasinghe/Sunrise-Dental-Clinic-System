<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.TreatmentType, java.util.List" %>
<%
    @SuppressWarnings("unchecked")
    List<TreatmentType> treatmentTypes = (List<TreatmentType>) request.getAttribute("treatmentTypes");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Treatments &amp; Prices - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="page-title">Treatments &amp; Prices</div>
        <div class="page-subtitle">Consultation fees for every treatment we offer &mdash; know the price before you book.</div>

        <div class="card">
            <% if (treatmentTypes == null || treatmentTypes.isEmpty()) { %>
                <div class="empty-state"><i class="bi bi-clipboard2-pulse"></i>No treatments are listed yet.</div>
            <% } else { %>
            <div class="table-wrap"><table class="data-table">
                <thead><tr><th>Treatment</th><th>Consultation Fee</th><th></th></tr></thead>
                <tbody>
                <% for (TreatmentType t : treatmentTypes) { %>
                    <tr>
                        <td><i class="bi bi-clipboard2-pulse" style="color:var(--primary); margin-right:8px;"></i><%= t.getTreatmentName() %></td>
                        <td><strong>LKR <%= String.format("%,.2f", t.getConsultationFee()) %></strong></td>
                        <td style="text-align:right;">
                            <a class="btn btn-secondary inline-btn" style="text-decoration:none;" href="${pageContext.request.contextPath}/doctors">
                                <i class="bi bi-calendar-plus"></i> Book
                            </a>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table></div>
            <div style="margin-top:14px; font-size:12px; color:var(--text-muted);">
                <i class="bi bi-info-circle"></i> Prices shown are the standard consultation fee for each treatment. Your assigned doctor may confirm any additional cost during your visit.
            </div>
            <% } %>
        </div>
        <a class="back-link" href="${pageContext.request.contextPath}/patientDashboard">&larr; Back to My Appointments</a>
    </div>
</body>
</html>
