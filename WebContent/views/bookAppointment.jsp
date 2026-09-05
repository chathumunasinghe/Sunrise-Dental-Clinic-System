<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Dentist, com.SunriseDental.Model.TreatmentType, java.util.List" %>
<%
    Dentist dentist = (Dentist) request.getAttribute("dentist");
    @SuppressWarnings("unchecked")
    List<TreatmentType> treatmentTypes = (List<TreatmentType>) request.getAttribute("treatmentTypes");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Book Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="page-title">Book Appointment</div>
        <div class="page-subtitle">Choose a treatment, date and time</div>

        <div class="card">
            <% if (dentist != null) { %>
            <div class="booking-summary">
                <i class="bi bi-person-badge"></i>
                <div>
                    <strong><%= dentist.getName() %></strong><br/>
                    <span style="color:var(--text-muted);"><%= dentist.getSpecialization() == null || dentist.getSpecialization().isBlank() ? "General Dentistry" : dentist.getSpecialization() %></span>
                </div>
            </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/bookAppointment" method="post">
                <input type="hidden" name="dentistId" value="<%= dentist == null ? "" : dentist.getDentistId() %>" />
                <div class="field">
                    <label>Treatment Type</label>
                    <select name="treatmentId" class="form-select" required>
                        <% if (treatmentTypes != null) { for (TreatmentType t : treatmentTypes) { %>
                            <option value="<%= t.getTreatmentId() %>"><%= t.getTreatmentName() %> &mdash; LKR <%= String.format("%,.2f", t.getConsultationFee()) %></option>
                        <% }} %>
                    </select>
                </div>
                <div class="form-row">
                    <div class="field">
                        <label>Appointment Date</label>
                        <input type="date" class="form-control" name="date" id="dateInput"
                               value="<%= request.getParameter("date") == null ? "" : request.getParameter("date") %>" required />
                    </div>
                    <div class="field">
                        <label>Appointment Time</label>
                        <select class="form-select" name="time" id="timeSelect" required disabled>
                            <option value="">Select a date first</option>
                        </select>
                        <small id="slotHint" style="color:var(--text-muted); display:block; margin-top:4px;"></small>
                    </div>
                </div>
                <button type="submit" class="btn"><i class="bi bi-calendar-check"></i> Confirm &amp; Continue to Payment</button>
            </form>
        </div>
        <a class="back-link" href="${pageContext.request.contextPath}/doctors">&larr; Back to Doctors</a>
    </div>

    <script>
    (function () {
        var ctx = "${pageContext.request.contextPath}";
        var dentistId = <%= dentist == null ? "null" : dentist.getDentistId() %>;
        var preselectedTime = "<%= request.getParameter("time") == null ? "" : request.getParameter("time") %>";
        var dateInput = document.getElementById('dateInput');
        var timeSelect = document.getElementById('timeSelect');
        var hint = document.getElementById('slotHint');

        // Never let a patient pick a date that's already passed.
        var today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);

        function setPlaceholder(text, disabled) {
            timeSelect.innerHTML = '';
            var opt = document.createElement('option');
            opt.value = '';
            opt.textContent = text;
            timeSelect.appendChild(opt);
            timeSelect.disabled = disabled;
        }

        function formatTime(hhmm) {
            var parts = hhmm.split(':');
            var h = parseInt(parts[0], 10);
            var m = parts[1];
            var ampm = h >= 12 ? 'PM' : 'AM';
            var h12 = h % 12;
            if (h12 === 0) h12 = 12;
            return h12 + ':' + m + ' ' + ampm;
        }

        function loadSlots() {
            var date = dateInput.value;
            hint.textContent = '';
            if (!dentistId || !date) {
                setPlaceholder('Select a date first', true);
                return;
            }
            setPlaceholder('Loading available times…', true);

            fetch(ctx + '/availableSlots?dentistId=' + dentistId + '&date=' + date)
                .then(function (r) { return r.json(); })
                .then(function (data) {
                    timeSelect.innerHTML = '';
                    if (data.slots && data.slots.length > 0) {
                        timeSelect.disabled = false;
                        var placeholder = document.createElement('option');
                        placeholder.value = '';
                        placeholder.textContent = 'Choose a time';
                        timeSelect.appendChild(placeholder);
                        data.slots.forEach(function (slot) {
                            var o = document.createElement('option');
                            o.value = slot;
                            o.textContent = formatTime(slot);
                            if (slot === preselectedTime) o.selected = true;
                            timeSelect.appendChild(o);
                        });
                    } else {
                        setPlaceholder('No times available', true);
                    }
                    if (data.message) hint.textContent = data.message;
                })
                .catch(function () {
                    setPlaceholder('Could not load times — try again', true);
                });
        }

        dateInput.addEventListener('change', loadSlots);
        if (dateInput.value) loadSlots();
    })();
    </script>
</body>
</html>
