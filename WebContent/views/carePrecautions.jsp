<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Patient" %>
<%
    Patient loggedInPatient = (Patient) session.getAttribute("patient");
    if (loggedInPatient == null) { response.sendRedirect(request.getContextPath() + "/views/login.jsp"); return; }
    String activeTreatment = request.getParameter("treatment");
    if (activeTreatment == null) activeTreatment = "General";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Care Instructions - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <style>
        .care-banner {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: #fff; border-radius: 16px; padding: 22px 26px; margin-bottom: 20px;
            display: flex; align-items: center; gap: 16px;
        }
        .care-banner i { font-size: 28px; }
        .care-banner h2 { margin: 0; font-size: 17px; }
        .care-banner p { margin: 2px 0 0; font-size: 12.5px; opacity: .9; }

        .care-tabs { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 18px; }
        .care-tab {
            text-decoration: none; font-size: 12.5px; font-weight: 600; color: var(--primary-dark);
            background: var(--primary-light); border: 1.5px solid transparent; border-radius: 999px;
            padding: 8px 16px; transition: background .15s, color .15s;
        }
        .care-tab.active { background: var(--primary-dark); color: #fff; }
        .care-tab:hover:not(.active) { background: #d6f0ec; }

        .care-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px; }
        @media (max-width: 760px) { .care-grid { grid-template-columns: 1fr; } }

        .care-section-title { display: flex; align-items: center; gap: 8px; font-size: 14.5px; font-weight: 700; color: var(--navy); margin: 0 0 12px; }
        .care-section-title i { color: var(--primary-dark); font-size: 17px; }
        .care-list { margin: 0; padding-left: 0; list-style: none; }
        .care-list li { display: flex; gap: 10px; padding: 8px 0; border-bottom: 1px dashed #eef1f2; font-size: 13px; line-height: 1.5; color: var(--text-dark); }
        .care-list li:last-child { border-bottom: none; }
        .care-list li i { color: var(--primary-dark); margin-top: 3px; flex-shrink: 0; }
        .care-list.warn li i { color: var(--danger); }

        .care-emergency {
            margin-top: 20px; background: #fef2f2; border: 1px solid #fecaca; border-radius: 14px;
            padding: 18px 20px; display: flex; align-items: flex-start; gap: 14px;
        }
        .care-emergency i { color: var(--danger); font-size: 22px; margin-top: 2px; }
        .care-emergency strong { color: #991b1b; }
        .care-emergency p { margin: 4px 0 0; font-size: 12.5px; color: #7f1d1d; }
    </style>
</head>
<body>
    <%@ include file="includes/patientTopbar.jsp" %>
    <div class="patient-page-wrapper">
        <div class="page-title">Treatment Care Instructions</div>
        <div class="page-subtitle">Please follow these precautions before and after your visit for a safe, comfortable recovery.</div>

        <div class="care-banner">
            <i class="bi bi-shield-check"></i>
            <div>
                <h2>Your safety matters to us</h2>
                <p>Select your treatment type below to see instructions specific to it, alongside general care that applies to every visit.</p>
            </div>
        </div>

        <div class="care-tabs">
            <a class="care-tab <%= "General".equals(activeTreatment) ? "active" : "" %>" href="?treatment=General">General / All Visits</a>
            <a class="care-tab <%= "Consultation".equals(activeTreatment) ? "active" : "" %>" href="?treatment=Consultation">Consultation</a>
            <a class="care-tab <%= "Tooth Filling".equals(activeTreatment) ? "active" : "" %>" href="?treatment=Tooth+Filling">Tooth Filling</a>
            <a class="care-tab <%= "Tooth Extraction".equals(activeTreatment) ? "active" : "" %>" href="?treatment=Tooth+Extraction">Tooth Extraction</a>
            <a class="care-tab <%= "Root Canal".equals(activeTreatment) ? "active" : "" %>" href="?treatment=Root+Canal">Root Canal</a>
        </div>

        <% if ("General".equals(activeTreatment)) { %>
        <div class="care-grid">
            <div class="card">
                <div class="care-section-title"><i class="bi bi-clipboard-check"></i>Before Your Appointment</div>
                <ul class="care-list">
                    <li><i class="bi bi-check-circle"></i>Arrive 10–15 minutes early to complete any paperwork.</li>
                    <li><i class="bi bi-check-circle"></i>Eat a light meal beforehand if you'll be numbed — it's harder to eat comfortably afterward.</li>
                    <li><i class="bi bi-check-circle"></i>Brush and floss as normal before you come in.</li>
                    <li><i class="bi bi-check-circle"></i>Bring a list of any medications or health conditions to mention to your dentist.</li>
                    <li><i class="bi bi-check-circle"></i>Tell the front desk about any allergies (including to latex or anaesthetics).</li>
                </ul>
            </div>
            <div class="card">
                <div class="care-section-title"><i class="bi bi-heart-pulse"></i>General Aftercare</div>
                <ul class="care-list">
                    <li><i class="bi bi-check-circle"></i>Continue gentle brushing twice a day and flossing once a day.</li>
                    <li><i class="bi bi-check-circle"></i>Avoid very hot or very cold food/drinks if the area still feels sensitive.</li>
                    <li><i class="bi bi-check-circle"></i>Mild soreness for a day or two is normal — over-the-counter pain relief can help if needed.</li>
                    <li><i class="bi bi-check-circle"></i>Keep your next follow-up appointment even if you feel fine.</li>
                    <li><i class="bi bi-check-circle"></i>Call the clinic if pain, swelling, or bleeding gets worse instead of better.</li>
                </ul>
            </div>
        </div>
        <% } %>

        <% if ("Consultation".equals(activeTreatment)) { %>
        <div class="card">
            <div class="care-section-title"><i class="bi bi-clipboard-pulse"></i>Consultation Visit</div>
            <ul class="care-list">
                <li><i class="bi bi-check-circle"></i>No special preparation is needed — just brush normally beforehand.</li>
                <li><i class="bi bi-check-circle"></i>Bring any previous X-rays or dental records if you have them.</li>
                <li><i class="bi bi-check-circle"></i>Write down any symptoms (pain, sensitivity, bleeding gums) so you don't forget to mention them.</li>
                <li><i class="bi bi-check-circle"></i>You can eat and drink normally right after a consultation.</li>
            </ul>
        </div>
        <% } %>

        <% if ("Tooth Filling".equals(activeTreatment)) { %>
        <div class="care-grid">
            <div class="card">
                <div class="care-section-title"><i class="bi bi-clipboard-check"></i>Before Filling</div>
                <ul class="care-list">
                    <li><i class="bi bi-check-circle"></i>Eat beforehand, since the treated side of your mouth may stay numb for a few hours.</li>
                    <li><i class="bi bi-check-circle"></i>Mention if you've ever had a reaction to local anaesthetic.</li>
                </ul>
            </div>
            <div class="card">
                <div class="care-section-title"><i class="bi bi-exclamation-triangle"></i>After Filling</div>
                <ul class="care-list warn">
                    <li><i class="bi bi-dot"></i>Avoid chewing on the filled side until numbness fully wears off (usually 2–4 hours) to prevent biting your cheek or tongue.</li>
                    <li><i class="bi bi-dot"></i>Mild sensitivity to hot/cold or biting pressure for a few days is normal.</li>
                    <li><i class="bi bi-dot"></i>Avoid very hard or sticky foods on that tooth for the first 24 hours.</li>
                    <li><i class="bi bi-dot"></i>Contact the clinic if sensitivity lasts more than 2 weeks or the bite feels uneven.</li>
                </ul>
            </div>
        </div>
        <% } %>

        <% if ("Tooth Extraction".equals(activeTreatment)) { %>
        <div class="care-grid">
            <div class="card">
                <div class="care-section-title"><i class="bi bi-clipboard-check"></i>Before Extraction</div>
                <ul class="care-list">
                    <li><i class="bi bi-check-circle"></i>Have a light meal beforehand — you may need to avoid heavy chewing for a while after.</li>
                    <li><i class="bi bi-check-circle"></i>Arrange a ride home if you're anxious or being sedated.</li>
                    <li><i class="bi bi-check-circle"></i>Tell your dentist about blood thinners or other regular medication.</li>
                </ul>
            </div>
            <div class="card">
                <div class="care-section-title"><i class="bi bi-exclamation-triangle"></i>After Extraction — First 24 Hours</div>
                <ul class="care-list warn">
                    <li><i class="bi bi-dot"></i>Bite gently on the gauze for 30–45 minutes to help a clot form; avoid rinsing or spitting forcefully.</li>
                    <li><i class="bi bi-dot"></i>No smoking, straws, or carbonated drinks — suction can dislodge the clot and cause a painful "dry socket".</li>
                    <li><i class="bi bi-dot"></i>Stick to soft, cool foods (yoghurt, soup, mashed rice/vegetables) and chew on the opposite side.</li>
                    <li><i class="bi bi-dot"></i>Use a cold pack on the outside of the cheek for swelling, on and off for 15 minutes at a time.</li>
                    <li><i class="bi bi-dot"></i>Keep your head slightly elevated when resting, and avoid strenuous exercise for a day or two.</li>
                    <li><i class="bi bi-dot"></i>From the next day, rinse gently with warm salt water after meals to keep the area clean.</li>
                </ul>
            </div>
        </div>
        <% } %>

        <% if ("Root Canal".equals(activeTreatment)) { %>
        <div class="care-grid">
            <div class="card">
                <div class="care-section-title"><i class="bi bi-clipboard-check"></i>Before Root Canal</div>
                <ul class="care-list">
                    <li><i class="bi bi-check-circle"></i>Eat a proper meal beforehand — numbness can make eating awkward for a few hours after.</li>
                    <li><i class="bi bi-check-circle"></i>Take any antibiotics or medication exactly as prescribed leading up to the visit.</li>
                    <li><i class="bi bi-check-circle"></i>Root canal treatment can take more than one visit — plan for a follow-up appointment.</li>
                </ul>
            </div>
            <div class="card">
                <div class="care-section-title"><i class="bi bi-exclamation-triangle"></i>After Root Canal</div>
                <ul class="care-list warn">
                    <li><i class="bi bi-dot"></i>Avoid chewing on the treated tooth until it's fully restored with a permanent filling or crown.</li>
                    <li><i class="bi bi-dot"></i>Mild tenderness for a few days is normal, especially when biting.</li>
                    <li><i class="bi bi-dot"></i>Keep the temporary filling intact — avoid hard, crunchy, or sticky foods on that side.</li>
                    <li><i class="bi bi-dot"></i>Attend your follow-up visit to have the permanent restoration placed — delaying can risk reinfection.</li>
                    <li><i class="bi bi-dot"></i>Contact the clinic if you get severe pain, swelling of the face, or a bad taste that doesn't go away.</li>
                </ul>
            </div>
        </div>
        <% } %>

        <div class="care-emergency">
            <i class="bi bi-telephone-forward"></i>
            <div>
                <strong>When to call us right away</strong>
                <p>Severe or worsening pain, heavy bleeding that doesn't stop after 20 minutes of steady pressure, facial swelling, fever, or an allergic reaction (rash, itching, difficulty breathing) need prompt attention — please call the clinic front desk or visit the nearest emergency department.</p>
            </div>
        </div>

        <a class="back-link" href="${pageContext.request.contextPath}/patientDashboard">&larr; Back to My Appointments</a>
    </div>
</body>
</html>
