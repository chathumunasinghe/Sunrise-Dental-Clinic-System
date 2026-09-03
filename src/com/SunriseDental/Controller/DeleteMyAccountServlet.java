package com.SunriseDental.Controller;

import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Lets a signed-in patient permanently delete their own portal account
 * (and every appointment/billing record tied to it) from "My Appointments".
 * Requires re-entering their current password so a session left open on a
 * shared device can't be used to wipe the account without it.
 */
@WebServlet("/deleteMyAccount")
public class DeleteMyAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        String password = req.getParameter("currentPassword");
        PatientDAO patientDAO = new PatientDAO();

        if (password == null || password.isBlank() || !patientDAO.checkPassword(patient.getPatientId(), password)) {
            req.setAttribute("accountDeleteError", "That password isn't correct — your account was not deleted.");
            req.setAttribute("myAppointments", new com.SunriseDental.Dao.AppointmentDAO().getByPatientId(patient.getPatientId()));
            com.SunriseDental.Dao.NotificationDAO notificationDAO = new com.SunriseDental.Dao.NotificationDAO();
            req.setAttribute("notifications", notificationDAO.getForPatient(patient.getPatientId()));
            req.getRequestDispatcher("/views/patientDashboard.jsp").forward(req, resp);
            return;
        }

        boolean deleted = patientDAO.deletePatientCascade(patient.getPatientId());
        if (!deleted) {
            req.setAttribute("accountDeleteError", "Could not delete your account right now. Please try again.");
            req.setAttribute("myAppointments", new com.SunriseDental.Dao.AppointmentDAO().getByPatientId(patient.getPatientId()));
            com.SunriseDental.Dao.NotificationDAO notificationDAO = new com.SunriseDental.Dao.NotificationDAO();
            req.setAttribute("notifications", notificationDAO.getForPatient(patient.getPatientId()));
            req.getRequestDispatcher("/views/patientDashboard.jsp").forward(req, resp);
            return;
        }

        session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/views/login.jsp?accountDeleted=1");
    }
}
