package com.SunriseDental.Controller;

import com.SunriseDental.Service.AppointmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/registerAppointment")
public class RegisterAppointmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Session guard: only authorized staff can register appointments
        if (req.getSession().getAttribute("staff") == null) {
            resp.sendRedirect("login");
            return;
        }

        String patientId = req.getParameter("patientId");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String contact = req.getParameter("contact");
        String email = req.getParameter("email");

        int dentistId;
        int treatmentId;
        Date date;
        String time = req.getParameter("time");

        try {
            dentistId = Integer.parseInt(req.getParameter("dentistId"));
            treatmentId = Integer.parseInt(req.getParameter("treatmentId"));
            date = Date.valueOf(req.getParameter("date"));
        } catch (IllegalArgumentException | NullPointerException e) {
            req.setAttribute("error", "Please fill in a valid dentist, treatment, and date.");
            req.getRequestDispatcher("/views/registerAppointment.jsp").forward(req, resp);
            return;
        }

        AppointmentService service = new AppointmentService();
        AppointmentService.RegistrationResult result = service.registerAppointmentDetailed(
                patientId, name, address, contact, email, dentistId, treatmentId, date, time);

        if (result != null) {
            StringBuilder msg = new StringBuilder("Appointment registered successfully. Number: ")
                    .append(result.appointmentNumber).append(". A confirmation has been sent to the patient.");
            if (result.generatedUsername != null) {
                msg.append(" This is a new patient — a portal login was created for them: username \"")
                        .append(result.generatedUsername).append("\", temporary password \"")
                        .append(result.generatedPassword)
                        .append("\". Please share these with the patient so they can log in and see this appointment themselves.");
            } else if (result.matchedExistingPatient) {
                msg.append(" Matched to existing patient ").append(result.patientId)
                        .append(result.alreadyHadPortalLogin
                                ? " — they can already see this in their portal login."
                                : " — note: this patient does not have a portal login yet.");
            }
            req.setAttribute("message", msg.toString());
        } else {
            req.setAttribute("error", "Registration failed. Please check the details entered.");
        }
        req.getRequestDispatcher("/views/registerAppointment.jsp").forward(req, resp);
    }
}
