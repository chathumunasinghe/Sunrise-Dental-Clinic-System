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
        String appointmentNumber = service.registerAppointment(
                patientId, name, address, contact, email, dentistId, treatmentId, date, time);

        if (appointmentNumber != null) {
            req.setAttribute("message", "Appointment registered successfully. Number: " + appointmentNumber
                    + ". A confirmation has been sent to the patient.");
        } else {
            req.setAttribute("error", "Registration failed. Please check the details entered.");
        }
        req.getRequestDispatcher("/views/registerAppointment.jsp").forward(req, resp);
    }
}
