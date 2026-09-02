package com.SunriseDental.Controller;

import com.SunriseDental.Dao.PatientDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Patient portal "Create Account" — self-registration, separate from the front-desk save(). */
@WebServlet("/patientRegister")
public class PatientRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/patientSignup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String contact = req.getParameter("contact");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        req.setAttribute("name", name);
        req.setAttribute("address", address);
        req.setAttribute("contact", contact);
        req.setAttribute("email", email);
        req.setAttribute("username", username);

        if (name == null || name.trim().isEmpty() || username == null || username.trim().isEmpty()
                || password == null || password.length() < 6) {
            req.setAttribute("error", "Please fill in your name, a username, and a password of at least 6 characters.");
            req.getRequestDispatcher("/views/patientSignup.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/views/patientSignup.jsp").forward(req, resp);
            return;
        }

        PatientDAO patientDAO = new PatientDAO();
        if (patientDAO.usernameExists(username.trim())) {
            req.setAttribute("error", "That username is already taken. Please choose another.");
            req.getRequestDispatcher("/views/patientSignup.jsp").forward(req, resp);
            return;
        }

        String patientId = patientDAO.registerPatient(
                name.trim(), address, contact, email, username.trim(), password);

        if (patientId != null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp?signup=success");
        } else {
            req.setAttribute("error", "Could not create your account. Please try again.");
            req.getRequestDispatcher("/views/patientSignup.jsp").forward(req, resp);
        }
    }
}
