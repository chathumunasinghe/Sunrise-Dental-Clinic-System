<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.SunriseDental.Model.Bill" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Billing - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <style>
        .receipt-doc {
            max-width: 620px;
            background: #fff;
            border-radius: 16px;
            box-shadow: var(--card-shadow);
            overflow: hidden;
            margin-top: 8px;
        }
        .receipt-head {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: #fff;
            padding: 26px 30px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .receipt-head .clinic-name { font-size: 19px; font-weight: 700; }
        .receipt-head .clinic-sub { font-size: 12px; opacity: .85; margin-top: 2px; }
        .receipt-status {
            font-size: 11px;
            font-weight: 700;
            letter-spacing: .04em;
            padding: 5px 12px;
            border-radius: 999px;
            text-transform: uppercase;
        }
        .receipt-status.paid { background: #dcfce7; color: #15803d; }
        .receipt-status.unpaid { background: #fef3c7; color: #b45309; }
        .receipt-body { padding: 26px 30px; }
        .receipt-meta {
            display: flex;
            justify-content: space-between;
            font-size: 12.5px;
            color: var(--text-muted);
            border-bottom: 1px dashed #e2e8f0;
            padding-bottom: 16px;
            margin-bottom: 18px;
        }
        .receipt-section-title {
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: .05em;
            color: var(--primary-dark);
            margin-bottom: 8px;
        }
        .receipt-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 4px 20px;
            font-size: 13.5px;
            margin-bottom: 20px;
        }
        .receipt-grid div span { color: var(--text-muted); }
        .receipt-line {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #f0f2f4;
            font-size: 14px;
        }
        .receipt-total {
            display: flex;
            justify-content: space-between;
            padding-top: 16px;
            font-size: 19px;
            font-weight: 700;
            color: var(--primary-dark);
        }
        .receipt-footer {
            text-align: center;
            font-size: 11.5px;
            color: var(--text-muted);
            padding: 16px 30px 24px;
        }
        @media print {
            .topbar, form, .back-link, .no-print, .page-title, .page-subtitle { display: none !important; }
            .page-wrapper { margin: 0; padding: 0; }
            .receipt-doc { box-shadow: none; max-width: 100%; }
        }
    </style>
</head>
<body>
    <%@ include file="includes/topbar.jsp" %>

    <div class="page-wrapper">
        <div class="page-title">Calculate &amp; Print Bill</div>
        <div class="page-subtitle">Generate a full receipt for a completed appointment</div>

        <div class="card">
            <form action="${pageContext.request.contextPath}/billing" method="get" style="display:flex; gap:12px; align-items:flex-end;">
                <div class="field" style="flex:1; margin-bottom:0;">
                    <label>Appointment Number</label>
                    <input type="text" class="form-control" name="appointmentNumber" placeholder="e.g. APT0001" required />
                </div>
                <button type="submit" class="btn" style="width:auto; padding:11px 24px;">Generate Bill</button>
            </form>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <%
            Bill bill = (Bill) request.getAttribute("bill");
            @SuppressWarnings("unchecked")
            Map<String, Object> r = (Map<String, Object>) request.getAttribute("receipt");
            if (bill != null && r != null) {
        %>
            <div class="receipt-doc">
                <div class="receipt-head">
                    <div>
                        <div class="clinic-name">&#129463;&nbsp; Sunrise Dental Clinic</div>
                        <div class="clinic-sub">Colombo, Sri Lanka &middot; Official Payment Receipt</div>
                    </div>
                    <span class="receipt-status <%= bill.isPaid() ? "paid" : "unpaid" %>">
                        <%= bill.isPaid() ? "Paid" : "Unpaid" %>
                    </span>
                </div>

                <div class="receipt-body">
                    <div class="receipt-meta">
                        <span>Receipt&nbsp;#<%= bill.getBillId() %></span>
                        <span>Issued: <%= bill.getIssueDate() %></span>
                    </div>

                    <div class="receipt-section-title">Patient Details</div>
                    <div class="receipt-grid">
                        <div><span>Name:</span> <strong><%= r.get("patient_name") %></strong></div>
                        <div><span>Contact:</span> <strong><%= r.get("patient_contact") %></strong></div>
                        <div><span>Email:</span> <strong><%= r.get("patient_email") == null ? "—" : r.get("patient_email") %></strong></div>
                        <div><span>Address:</span> <strong><%= r.get("patient_address") == null ? "—" : r.get("patient_address") %></strong></div>
                    </div>

                    <div class="receipt-section-title">Appointment Details</div>
                    <div class="receipt-grid" style="margin-bottom: 6px;">
                        <div><span>Appointment #:</span> <strong><%= bill.getAppointmentNumber() %></strong></div>
                        <div><span>Status:</span> <strong><%= r.get("status") %></strong></div>
                        <div><span>Date:</span> <strong><%= r.get("appointment_date") %></strong></div>
                        <div><span>Time:</span> <strong><%= r.get("appointment_time") %></strong></div>
                        <div><span>Dentist:</span> <strong><%= r.get("dentist_name") %></strong></div>
                        <div><span>Specialization:</span> <strong><%= r.get("specialization") %></strong></div>
                    </div>

                    <div class="receipt-line">
                        <span><%= r.get("treatment_name") %></span>
                        <span>LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></span>
                    </div>
                    <div class="receipt-total">
                        <span>Total</span>
                        <span>LKR <%= String.format("%,.2f", bill.getTotalAmount()) %></span>
                    </div>
                </div>

                <div class="receipt-footer">
                    Thank you for choosing Sunrise Dental Clinic. This is a system-generated receipt.
                </div>
            </div>

            <div class="no-print" style="margin-top:16px; display:flex; gap:10px;">
                <button onclick="window.print()" class="btn" style="width:auto; padding:10px 22px;">
                    <i class="bi bi-printer"></i>&nbsp;Print Receipt
                </button>
                <% if (!bill.isPaid()) { %>
                <a class="btn btn-secondary" style="width:auto; padding:10px 22px;"
                   href="${pageContext.request.contextPath}/pay?appointmentNumber=<%= bill.getAppointmentNumber() %>">
                    <i class="bi bi-credit-card"></i>&nbsp;Pay Online
                </a>
                <% } %>
            </div>
        <% } %>
        <br/>
        <a class="back-link" href="${pageContext.request.contextPath}/dashboard">&larr; Back to Dashboard</a>
    </div>
</body>
</html>
