package com.SunriseDental.Controller;

import com.SunriseDental.Dao.StaffDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username != null) username = username.trim();
        if (password != null) password = password.trim();

        Staff staff;
        try {
            StaffDAO staffDAO = new StaffDAO();
            staff = staffDAO.validateLogin(username, password);
        } catch (RuntimeException dbError) {
            // Surface DB/connection problems as a clear on-page message
            // instead of letting them crash into a blank server error page.
            dbError.printStackTrace();
            req.setAttribute("error", "We couldn't reach the database right now. Please check that MySQL is "
                    + "running and the schema is up to date, then try again. (" + dbError.getMessage() + ")");
            req.setAttribute("username", username);
            req.setAttribute("activeTab", "staff");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            return;
        }

        if (staff != null) {
            HttpSession session = req.getSession();
            session.setAttribute("staff", staff);
            session.setAttribute("role", staff.getRole());
            resp.sendRedirect(req.getContextPath() + (staff.isDentist() ? "/dentistDashboard" : "/dashboard"));
        } else {
            req.setAttribute("error", "Invalid username or password.");
            req.setAttribute("username", username);
            req.setAttribute("activeTab", "staff");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("activeTab", "staff");
        req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
    }
}
