package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * A dentist's own dashboard — shows only appointments assigned to them
 * (via staff.dentist_id), so they see their own schedule and can view
 * patient treatment info / add treatment notes, without visibility into
 * the rest of the clinic's appointments, billing, or staff management.
 */
@WebServlet("/dentistDashboard")
public class DentistDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Staff staff = (session != null) ? (Staff) session.getAttribute("staff") : null;

        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        if (!staff.isDentist() || staff.getDentistId() == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        req.setAttribute("myAppointments", appointmentDAO.getByDentistId(staff.getDentistId()));
        req.getRequestDispatcher("/views/dentistDashboard.jsp").forward(req, resp);
    }
}
