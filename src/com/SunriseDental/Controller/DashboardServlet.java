package com.SunriseDental.Controller;

import com.SunriseDental.Dao.DashboardDAO;
import com.SunriseDental.Model.Staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Staff staff = session == null ? null : (Staff) session.getAttribute("staff");
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        if (staff.isDentist()) {
            resp.sendRedirect(req.getContextPath() + "/dentistDashboard");
            return;
        }

        try {
            DashboardDAO dao = new DashboardDAO();
            req.setAttribute("todayAppointments", dao.getTodayAppointments());
            req.setAttribute("totalPatients", dao.getTotalPatients());
            req.setAttribute("activeStaff", dao.getActiveStaff());
            req.setAttribute("todayRevenue", dao.getTodayRevenue());
            req.setAttribute("todaySchedule", dao.getTodaySchedule(6));
        } catch (RuntimeException dbError) {
            dbError.printStackTrace();
            req.setAttribute("error", "We couldn't reach the database right now. Please check that MySQL is "
                    + "running and the schema is up to date. (" + dbError.getMessage() + ")");
        }
        req.getRequestDispatcher("/views/dashboard.jsp").forward(req, resp);
    }
}
