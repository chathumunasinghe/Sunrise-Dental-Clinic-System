package com.SunriseDental.Controller;

import com.SunriseDental.Dao.StaffDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");
        StaffDAO staffDAO = new StaffDAO();

        if (token == null || staffDAO.validateResetToken(token) == -1) {
            req.setAttribute("error", "This reset link is invalid or has expired.");
        } else {
            req.setAttribute("token", token);
        }
        req.getRequestDispatcher("/views/resetPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        StaffDAO staffDAO = new StaffDAO();
        int staffId = staffDAO.validateResetToken(token);

        if (staffId == -1) {
            req.setAttribute("error", "This reset link is invalid or has expired.");
            req.getRequestDispatcher("/views/resetPassword.jsp").forward(req, resp);
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters long.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/resetPassword.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/resetPassword.jsp").forward(req, resp);
            return;
        }

        if (staffDAO.resetPassword(token, staffId, newPassword)) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp?reset=success");
        } else {
            req.setAttribute("error", "Unable to update the password. Please request a new reset link.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/views/resetPassword.jsp").forward(req, resp);
        }
    }
}
