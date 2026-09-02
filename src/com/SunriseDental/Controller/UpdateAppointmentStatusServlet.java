package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Lets front-desk staff — GUEST or ADMIN — update an appointment's status
 * from the dashboard or search-appointment screens, e.g. marking a visit
 * "Completed" once the patient has met the doctor.
 */
@WebServlet("/updateAppointmentStatus")
public class UpdateAppointmentStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final java.util.Set<String> ALLOWED_STATUSES =
            java.util.Set.of("Scheduled", "Completed", "Cancelled");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Staff staff = session == null ? null : (Staff) session.getAttribute("staff");
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        String status = req.getParameter("status");
        String returnTo = req.getParameter("returnTo");

        if (appointmentNumber != null && status != null && ALLOWED_STATUSES.contains(status)) {
            new AppointmentDAO().updateStatus(appointmentNumber, status);
        }

        if ("search".equals(returnTo)) {
            resp.sendRedirect(req.getContextPath() + "/searchAppointment?appointmentNumber=" + appointmentNumber);
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
