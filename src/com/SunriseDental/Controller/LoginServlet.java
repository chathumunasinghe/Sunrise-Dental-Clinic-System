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

        StaffDAO staffDAO = new StaffDAO();
        Staff staff = staffDAO.validateLogin(username, password);

        if (staff != null) {
            HttpSession session = req.getSession();
            session.setAttribute("staff", staff);
            session.setAttribute("role", staff.getRole());
            resp.sendRedirect(req.getContextPath() + "/dashboard");
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
