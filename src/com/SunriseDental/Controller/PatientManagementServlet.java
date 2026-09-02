package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Admin-only screen: a full patient directory plus every appointment's
 * doctor and treatment details in one place. AuthFilter already blocks
 * GUEST users from reaching the view directly, but the role is re-checked
 * here too since a servlet can be hit directly.
 */
@WebServlet("/managePatients")
public class PatientManagementServlet extends HttpServlet {

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
        if (!staff.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        PatientDAO patientDAO = new PatientDAO();
        AppointmentDAO appointmentDAO = new AppointmentDAO();

        req.setAttribute("patientList", patientDAO.getPatientDirectory());
        req.setAttribute("historyList", appointmentDAO.getFullHistory());
        req.getRequestDispatcher("/views/managePatients.jsp").forward(req, resp);
    }
}
