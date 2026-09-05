package com.SunriseDental.Controller;

import com.SunriseDental.Dao.PatientDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/patientResetPassword")
public class PatientResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");
        PatientDAO patientDAO = new PatientDAO();

        if (token == null || patientDAO.validateResetToken(token) == null) {
            req.setAttribute("error", "This reset link is invalid or has expired.");
        } else {
            req.setAttribute("token", token);
        }
        req.getRequestDispatcher("/views/patientResetPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        PatientDAO patientDAO = new PatientDAO();
        String patientId = patientDAO.validateResetToken(token);

        if (patientId == null) {
            req.setAttribute("error", "This reset link is invalid or has expired.");
            req.getRequestDispatcher("/views/patientResetPassword.jsp").forward(req, resp);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters long.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/patientResetPassword.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/patientResetPassword.jsp").forward(req, resp);
            return;
        }

        if (patientDAO.resetPassword(token, patientId, newPassword)) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp?reset=success");
        } else {
            req.setAttribute("error", "Unable to update the password. Please request a new reset link.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/patientResetPassword.jsp").forward(req, resp);
        }
    }
}
