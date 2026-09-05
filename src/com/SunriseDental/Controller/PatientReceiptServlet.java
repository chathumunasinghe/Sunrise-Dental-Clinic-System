package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.BillDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Bill;
import com.SunriseDental.Model.Patient;
import com.SunriseDental.Service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Lets a logged-in patient view and print the receipt for their OWN
 * appointment. The ownership check (appointment.patientId must match the
 * session's patient) prevents a patient from viewing someone else's bill
 * just by guessing/changing the appointmentNumber in the URL.
 */
@WebServlet("/myReceipt")
public class PatientReceiptServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Patient patient = (session != null) ? (Patient) session.getAttribute("patient") : null;

        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp?as=patient");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/patientDashboard");
            return;
        }

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        Appointment appointment = appointmentDAO.findByNumber(appointmentNumber.trim());

        // Ownership check: this appointment must belong to the logged-in patient.
        if (appointment == null || !appointment.getPatientId().equals(patient.getPatientId())) {
            req.setAttribute("error", "That receipt could not be found for your account.");
            req.getRequestDispatcher("/views/patientDashboard.jsp").forward(req, resp);
            return;
        }

        BillDAO billDAO = new BillDAO();
        Bill bill = billDAO.findByAppointmentNumber(appointmentNumber.trim());

        if (bill == null) {
            req.setAttribute("error", "No bill has been generated for this appointment yet. Please check back after your visit.");
            req.getRequestDispatcher("/views/patientDashboard.jsp").forward(req, resp);
            return;
        }

        BillingService billingService = new BillingService();
        req.setAttribute("bill", bill);
        req.setAttribute("receipt", billingService.getReceiptDetails(appointmentNumber.trim()));
        req.getRequestDispatcher("/views/myReceipt.jsp").forward(req, resp);
    }
}
