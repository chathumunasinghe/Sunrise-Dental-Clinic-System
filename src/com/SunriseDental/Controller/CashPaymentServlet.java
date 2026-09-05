package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.BillDAO;
import com.SunriseDental.Dao.NotificationDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Bill;
import com.SunriseDental.Model.Staff;
import com.SunriseDental.Service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Front-desk action: when a patient pays their bill in cash at the clinic
 * (instead of through the patient portal's online checkout), an admin or
 * guest/receptionist marks it paid here from the Billing screen. Only
 * ADMIN and GUEST staff can do this — dentists don't handle payments.
 */
@WebServlet("/markCashPaid")
public class CashPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Staff staff = session == null ? null : (Staff) session.getAttribute("staff");
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        if (!(staff.isAdmin() || staff.isReceptionist())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        BillingService billingService = new BillingService();
        BillDAO billDAO = new BillDAO();

        if (appointmentNumber != null) {
            appointmentNumber = appointmentNumber.trim();
        }

        Bill bill = appointmentNumber == null || appointmentNumber.isEmpty() ? null
                : billDAO.findByAppointmentNumber(appointmentNumber);

        if (bill == null) {
            req.setAttribute("error", "Generate the bill first, then mark it paid.");
        } else if (bill.isPaid()) {
            req.setAttribute("error", "This bill is already marked as paid.");
        } else if (billDAO.markPaid(appointmentNumber, "CASH")) {
            req.setAttribute("message", "Payment recorded — " + appointmentNumber + " is now marked as paid (cash).");
            Appointment appointment = new AppointmentDAO().findByNumber(appointmentNumber);
            if (appointment != null) {
                new NotificationDAO().create(appointment.getPatientId(), appointmentNumber,
                        "We've received your cash payment for appointment " + appointmentNumber + ". Thank you!");
            }
        } else {
            req.setAttribute("error", "Could not update that bill. Please try again.");
        }

        // Re-render the receipt with the up-to-date paid status.
        if (appointmentNumber != null && !appointmentNumber.isEmpty()) {
            req.setAttribute("bill", billDAO.findByAppointmentNumber(appointmentNumber));
            req.setAttribute("receipt", billingService.getReceiptDetails(appointmentNumber));
        }
        req.getRequestDispatcher("/views/billing.jsp").forward(req, resp);
    }
}
