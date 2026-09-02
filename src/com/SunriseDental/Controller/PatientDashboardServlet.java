package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Patient portal home: their profile plus every appointment they've booked. */
@WebServlet("/patientDashboard")
public class PatientDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        req.setAttribute("myAppointments", appointmentDAO.getByPatientId(patient.getPatientId()));
        req.getRequestDispatcher("/views/patientDashboard.jsp").forward(req, resp);
    }
}
