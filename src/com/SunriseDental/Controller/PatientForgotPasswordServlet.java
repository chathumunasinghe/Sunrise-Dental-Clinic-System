package com.SunriseDental.Controller;

import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** "Forgot password" for the patient portal — same demo-safe pattern as the staff flow. */
@WebServlet("/patientForgotPassword")
public class PatientForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/patientForgotPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String identifier = req.getParameter("identifier");
        PatientDAO patientDAO = new PatientDAO();
        Patient patient = patientDAO.findByUsernameOrEmail(identifier);

        if (patient != null && patient.getUsername() != null) {
            String token = patientDAO.createPasswordResetToken(patient.getPatientId());
            if (token != null) {
                String resetLink = req.getContextPath() + "/patientResetPassword?token=" + token;
                req.setAttribute("resetLink", resetLink);
            }
        }

        req.setAttribute("message",
            "If a patient account matches those details, a password reset link has been sent.");
        req.getRequestDispatcher("/views/patientForgotPassword.jsp").forward(req, resp);
    }
}
