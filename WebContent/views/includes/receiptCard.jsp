<%@ page import="com.SunriseDental.Model.Bill" %>
<%@ page import="java.util.Map" %>
<%--
    Self-contained: pulls its own data straight from request attributes
    ("bill" and "receipt") rather than relying on the including page to have
    declared local scriptlet variables with matching names. Both billing.jsp
    and myReceipt.jsp set these two request attributes before including this
    fragment.
--%>
<%
    Bill bill = (Bill) request.getAttribute("bill");
    @SuppressWarnings("unchecked")
    Map<String, Object> r = (Map<String, Object>) request.getAttribute("receipt");
%>
<div class="receipt-doc">
    <div class="receipt-head">
        <div class="receipt-head-row">
            <div class="clinic-mark">
                <span class="tooth">&#129463;</span>
                <div>
                    <div class="clinic-name">Sunrise Dental Clinic</div>
                    <div class="clinic-sub">Colombo, Sri Lanka &middot; Official Payment Receipt</div>
                </div>
            </div>
            <span class="receipt-status <%= bill.isPaid() ? "paid" : "unpaid" %>">
                <%= bill.isPaid() ? ("Paid" + (bill.getPaymentMethod() != null ? " &middot; " + bill.getPaymentMethod() : "")) : "Payment Due" %>
            </span>
        </div>
        <div class="receipt-id-strip">
            <div>Receipt Number<strong>#<%= String.format("%05d", bill.getBillId()) %></strong></div>
            <div style="text-align:right;">Issued<strong><%= bill.getIssueDate() %></strong></div>
        </div>
    </div>

    <div class="receipt-body">
        <div class="receipt-panel">
            <div class="receipt-section-title"><i class="bi bi-person-fill"></i> Patient Details</div>
            <div class="receipt-grid">
                <div><span>Name</span><strong><%= r.get("patient_name") %></strong></div>
                <div><span>Contact</span><strong><%= r.get("patient_contact") %></strong></div>
                <div><span>Email</span><strong><%= r.get("patient_email") == null ? "&mdash;" : r.get("patient_email") %></strong></div>
                <div><span>Address</span><strong><%= r.get("patient_address") == null ? "&mdash;" : r.get("patient_address") %></strong></div>
            </div>
        </div>

        <div class="receipt-panel">
            <div class="receipt-section-title"><i class="bi bi-calendar2-heart"></i> Appointment Details</div>
            <div class="receipt-grid">
                <div><span>Appointment #</span><strong><%= bill.getAppointmentNumber() %></strong></div>
                <div><span>Status</span><strong><%= r.get("status") %></strong></div>
                <div><span>Date</span><strong><%= r.get("appointment_date") %></strong></div>
                <div><span>Time</span><strong><%= r.get("appointment_time") %></strong></div>
                <div><span>Dentist</span><strong><%= r.get("dentist_name") %></strong></div>
                <div><span>Specialization</span><strong><%= r.get("specialization") == null || String.valueOf(r.get("specialization")).isBlank() ? "General Dentistry" : r.get("specialization") %></strong></div>
            </div>
        </div>

        <div class="receipt-panel">
            <div class="receipt-line">
                <span><%= r.get("treatment_name") %><small>Consultation &amp; treatment fee</small></span>
                <span>LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></span>
            </div>
            <div class="receipt-total-row">
                <span class="label">Total Amount</span>
                <span class="amount">LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></span>
            </div>
        </div>

        <div class="receipt-footer">
            <div class="thanks">Thank you for choosing Sunrise Dental Clinic</div>
            This is a system-generated receipt and does not require a signature.
        </div>
    </div>
</div>
