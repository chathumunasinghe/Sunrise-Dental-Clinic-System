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
 * Admin-only function: enable/disable a doctor's ability to accept new
 * patient-facing bookings. A DISABLED doctor is hidden from the patient
 * "Meet Our Doctors" list and the front-desk "New Appointment" dropdown,
 * but their existing appointments and history are left untouched.
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

        req.setAttribute("dentists", dentistDAO.getAllDentists());
        req.getRequestDispatcher("/views/manageDoctors.jsp").forward(req, resp);
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        Staff staff = (Staff) session.getAttribute("staff");
        return staff != null && staff.isAdmin();
    }
}
