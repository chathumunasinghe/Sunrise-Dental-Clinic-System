package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.BillDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Bill;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Pay a treatment bill online. This is a demo checkout — no real card
 * processor is involved, but it models a real online-payment flow: show
 * the amount due, collect (fake) card details, then mark the bill paid.
 */
@WebServlet("/pay")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        Appointment appointment = appointmentNumber == null ? null
                : new AppointmentDAO().findByNumber(appointmentNumber);

        // Only the owning patient can view/pay their own bill.
        if (appointment == null || !appointment.getPatientId().equals(patient.getPatientId())) {
            resp.sendRedirect(req.getContextPath() + "/patientDashboard");
            return;
        }

        BillDAO billDAO = new BillDAO();
        Bill bill = billDAO.findByAppointmentNumber(appointmentNumber);
        req.setAttribute("appointment", appointment);
        req.setAttribute("bill", bill);
        if (bill != null) {
            req.setAttribute("receipt", billDAO.getReceiptDetails(appointmentNumber));
        }
        req.getRequestDispatcher("/views/payment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        Appointment appointment = appointmentNumber == null ? null
                : new AppointmentDAO().findByNumber(appointmentNumber);

        if (appointment == null || !appointment.getPatientId().equals(patient.getPatientId())) {
            resp.sendRedirect(req.getContextPath() + "/patientDashboard");
            return;
        }

        BillDAO billDAO = new BillDAO();
        billDAO.markPaid(appointmentNumber);

        req.setAttribute("appointment", appointment);
        req.setAttribute("bill", billDAO.findByAppointmentNumber(appointmentNumber));
        req.setAttribute("receipt", billDAO.getReceiptDetails(appointmentNumber));
        req.setAttribute("paymentSuccess", true);
        req.getRequestDispatcher("/views/payment.jsp").forward(req, resp);
    }
}
