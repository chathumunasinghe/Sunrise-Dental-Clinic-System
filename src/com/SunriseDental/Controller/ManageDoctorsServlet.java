package com.SunriseDental.Controller;

import com.SunriseDental.Dao.DentistDAO;
import com.SunriseDental.Model.Dentist;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Admin-only screen: create new doctor accounts (a dentist profile plus a
 * linked staff login in one step) and enable/disable a doctor's ability to
 * accept new patient-facing bookings. A DISABLED doctor is hidden from the
 * patient "Meet Our Doctors" list and the front-desk "New Appointment"
 * dropdown, but their existing appointments and history are left untouched.
 */
@WebServlet("/manageDoctors")
public class ManageDoctorsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        List<Dentist> dentists = new DentistDAO().getAllDentists();
        req.setAttribute("dentists", dentists);
        req.getRequestDispatcher("/views/manageDoctors.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        DentistDAO dentistDAO = new DentistDAO();
        String action = req.getParameter("action");

        if ("addDoctor".equals(action)) {
            handleAddDoctor(req, dentistDAO);
        } else {
            try {
                int dentistId = Integer.parseInt(req.getParameter("dentistId"));
                String newStatus = req.getParameter("newStatus");
                if (!("ACTIVE".equals(newStatus) || "DISABLED".equals(newStatus))) {
                    req.setAttribute("error", "That status change is not allowed.");
                } else if (dentistDAO.setStatus(dentistId, newStatus)) {
                    req.setAttribute("message", "Dr. " + newStatus.toLowerCase() + " status updated successfully.");
                } else {
                    req.setAttribute("error", "Unable to update that doctor's status.");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid doctor selected.");
            }
        }

        req.setAttribute("dentists", dentistDAO.getAllDentists());
        req.getRequestDispatcher("/views/manageDoctors.jsp").forward(req, resp);
    }

    private void handleAddDoctor(HttpServletRequest req, DentistDAO dentistDAO) {
        String name = trimOrEmpty(req.getParameter("name"));
        String specialization = trimOrEmpty(req.getParameter("specialization"));
        String qualification = trimOrEmpty(req.getParameter("qualification"));
        String bio = trimOrEmpty(req.getParameter("bio"));
        String email = trimOrEmpty(req.getParameter("email"));
        String consultationDays = trimOrEmpty(req.getParameter("consultationDays"));
        String username = trimOrEmpty(req.getParameter("username"));
        String password = req.getParameter("password");
        String experienceParam = req.getParameter("experienceYears");

        Integer experienceYears = null;
        if (experienceParam != null && !experienceParam.trim().isEmpty()) {
            try {
                experienceYears = Integer.parseInt(experienceParam.trim());
            } catch (NumberFormatException ignored) {
                // left null — treated as "not specified" below
            }
        }

        if (name.isEmpty() || email.isEmpty() || username.length() < 3
                || password == null || password.length() < 6) {
            req.setAttribute("error", "Please fill in the doctor's name, email, and a username/password (min 6 characters).");
            return;
        }

        int dentistId = dentistDAO.createDoctorAccount(name, specialization, qualification,
                experienceYears, bio, email, consultationDays, username, password);

        if (dentistId > 0) {
            req.setAttribute("message", "Dr. " + name + "'s profile and login were created successfully.");
        } else {
            req.setAttribute("error", "Could not create that doctor account. The username or email may already be in use.");
        }
    }

    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        Staff staff = (Staff) session.getAttribute("staff");
        return staff != null && staff.isAdmin();
    }
}
