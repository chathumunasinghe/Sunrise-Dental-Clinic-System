package com.SunriseDental.Controller;

import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/patientLogin")
public class PatientLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("patientUsername");
        String password = req.getParameter("patientPassword");
        if (username != null) username = username.trim();
        if (password != null) password = password.trim();

        Patient patient;
        try {
            PatientDAO patientDAO = new PatientDAO();
            patient = patientDAO.validateLogin(username, password);
        } catch (RuntimeException dbError) {
            dbError.printStackTrace();
            req.setAttribute("patientError", "We couldn't reach the database right now. Please check that MySQL "
                    + "is running and the schema is up to date, then try again. (" + dbError.getMessage() + ")");
            req.setAttribute("patientUsername", username);
            req.setAttribute("activeTab", "patient");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            return;
        }

        if (patient != null) {
            HttpSession session = req.getSession();
            session.setAttribute("patient", patient);
            resp.sendRedirect(req.getContextPath() + "/patientDashboard");
        } else {
            req.setAttribute("patientError", "Invalid username or password.");
            req.setAttribute("patientUsername", username);
            req.setAttribute("activeTab", "patient");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
    }
}
