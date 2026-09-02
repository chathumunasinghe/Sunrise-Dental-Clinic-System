package com.SunriseDental.Controller;

import com.SunriseDental.Dao.StaffDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Admin-only function: create new staff logins (ADMIN or GUEST role) and
 * enable/disable existing accounts. AuthFilter already blocks GUEST users
 * from reaching this servlet's view, but the role is re-checked here too
 * since a servlet can be hit directly.
 */
@WebServlet("/manageStaff")
public class StaffManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        StaffDAO staffDAO = new StaffDAO();
        List<Staff> staffList = staffDAO.getAllStaff();
        req.setAttribute("staffList", staffList);
        req.getRequestDispatcher("/views/manageStaff.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        String action = req.getParameter("action");
        StaffDAO staffDAO = new StaffDAO();

        if ("add".equals(action)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String role = req.getParameter("role");
            username = username == null ? "" : username.trim();
            fullName = fullName == null ? "" : fullName.trim();
            email = email == null ? "" : email.trim();
            if (username.length() < 3 || password == null || password.length() < 6 || fullName.isBlank() || email.isBlank()
                    || !("ADMIN".equals(role) || "GUEST".equals(role))) {
                req.setAttribute("error", "Please enter valid staff details. Passwords must contain at least 6 characters.");
            } else if (staffDAO.addStaff(username, password, fullName, email, role)) {
                req.setAttribute("message", "Staff account created successfully.");
            } else {
                req.setAttribute("error", "Unable to create the account. The username or email may already be in use.");
            }
        } else if ("toggleStatus".equals(action)) {
            try {
                int staffId = Integer.parseInt(req.getParameter("staffId"));
                String newStatus = req.getParameter("newStatus");
                Staff current = (Staff) req.getSession().getAttribute("staff");
                if (staffId == current.getStaffId() || !("ACTIVE".equals(newStatus) || "DISABLED".equals(newStatus))) {
                    req.setAttribute("error", "That account status change is not allowed.");
                } else if (staffDAO.setStaffStatus(staffId, newStatus)) {
                    req.setAttribute("message", "Account status updated successfully.");
                } else {
                    req.setAttribute("error", "Unable to update the staff account.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid staff account selected.");
            }
        } else if ("delete".equals(action)) {
            try {
                int staffId = Integer.parseInt(req.getParameter("staffId"));
                Staff current = (Staff) req.getSession().getAttribute("staff");
                if (staffId == current.getStaffId()) {
                    req.setAttribute("error", "You cannot delete the account you are currently logged in with.");
                } else if (staffDAO.deleteStaff(staffId)) {
                    req.setAttribute("message", "Staff account deleted permanently.");
                } else {
                    req.setAttribute("error", "Unable to delete that staff account.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid staff account selected.");
            }
        }

        req.setAttribute("staffList", staffDAO.getAllStaff());
        req.getRequestDispatcher("/views/manageStaff.jsp").forward(req, resp);
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        Staff staff = (Staff) session.getAttribute("staff");
        return staff != null && staff.isAdmin();
    }
}
