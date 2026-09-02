package com.SunriseDental.Controller;

import com.SunriseDental.Dao.DentistDAO;
import com.SunriseDental.Dao.TreatmentTypeDAO;
import com.SunriseDental.Model.Dentist;
import com.SunriseDental.Model.Patient;
import com.SunriseDental.Service.AppointmentService;
import com.SunriseDental.Service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

/**
 * Patient-facing appointment booking: pick a doctor (from the Doctors
 * page), choose a treatment/date/time, then move on to online payment.
 */
@WebServlet("/bookAppointment")
public class BookAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        int dentistId;
        try {
            dentistId = Integer.parseInt(req.getParameter("dentistId"));
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendRedirect(req.getContextPath() + "/doctors");
            return;
        }

        Dentist dentist = new DentistDAO().findById(dentistId);
        if (dentist == null) {
            resp.sendRedirect(req.getContextPath() + "/doctors");
            return;
        }

        req.setAttribute("dentist", dentist);
        req.setAttribute("treatmentTypes", new TreatmentTypeDAO().getAllTreatmentTypes());
        req.getRequestDispatcher("/views/bookAppointment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        int dentistId;
        int treatmentId;
        Date date;
        String time = req.getParameter("time");

        try {
            dentistId = Integer.parseInt(req.getParameter("dentistId"));
            treatmentId = Integer.parseInt(req.getParameter("treatmentId"));
            date = Date.valueOf(req.getParameter("date"));
        } catch (IllegalArgumentException | NullPointerException e) {
            req.setAttribute("error", "Please choose a valid treatment, date, and time.");
            req.setAttribute("dentist", new DentistDAO().findById(
                    tryParse(req.getParameter("dentistId"))));
            req.setAttribute("treatmentTypes", new TreatmentTypeDAO().getAllTreatmentTypes());
            req.getRequestDispatcher("/views/bookAppointment.jsp").forward(req, resp);
            return;
        }

        AppointmentService appointmentService = new AppointmentService();
        String appointmentNumber = appointmentService.bookForPatient(
                patient.getPatientId(), dentistId, treatmentId, date, time);

        if (appointmentNumber == null) {
            req.setAttribute("error", "Booking failed. Please try again.");
            req.setAttribute("dentist", new DentistDAO().findById(dentistId));
            req.setAttribute("treatmentTypes", new TreatmentTypeDAO().getAllTreatmentTypes());
            req.getRequestDispatcher("/views/bookAppointment.jsp").forward(req, resp);
            return;
        }

        // Generate the bill immediately so the patient can pay online right away.
        new BillingService().generateBill(appointmentNumber);
        resp.sendRedirect(req.getContextPath() + "/pay?appointmentNumber=" + appointmentNumber);
    }

    private int tryParse(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
