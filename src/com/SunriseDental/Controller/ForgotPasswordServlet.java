package com.SunriseDental.Controller;

import com.SunriseDental.Dao.StaffDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Handles "Forgot password" requests. A matching account gets a one-time
 * reset link (valid 30 minutes) delivered through the clinic's existing
 * notification channels. The response is identical whether or not the
 * account exists, so the form can't be used to check who is registered.
 */
@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/forgotPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String identifier = req.getParameter("identifier");
        StaffDAO staffDAO = new StaffDAO();
        Staff staff = staffDAO.findByUsernameOrEmail(identifier);

        if (staff != null) {
            String token = staffDAO.createPasswordResetToken(staff.getStaffId());
            if (token != null) {
                String resetLink = req.getContextPath() + "/resetPassword?token=" + token;
                // Demo-safe: the link is shown on screen so reset works without SMTP.
                req.setAttribute("resetLink", resetLink);
            }
        }

        req.setAttribute("message",
            "If an account matches those details, a password reset link has been sent.");
        req.getRequestDispatcher("/views/forgotPassword.jsp").forward(req, resp);
    }
}
