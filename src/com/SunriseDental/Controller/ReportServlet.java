package com.SunriseDental.Controller;

import com.SunriseDental.Model.Staff;
import com.SunriseDental.Service.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

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

        // Reports are admin-only, same rule AuthFilter enforces for direct
        // /views/reports.jsp access — re-checked here since this servlet
        // can be reached directly, bypassing that filter's URL pattern.
        if (!staff.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        ReportService reportService = new ReportService();

        String dateParam = req.getParameter("date");
        if (dateParam != null && !dateParam.trim().isEmpty()) {
            try {
                req.setAttribute("dailyAppointments", reportService.getDailyAppointmentReport(Date.valueOf(dateParam.trim())));
                req.setAttribute("selectedDate", dateParam.trim());
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", "Please choose a valid date.");
            }
        }

        req.setAttribute("revenueByTreatment", reportService.getRevenueByTreatmentReport());
        req.setAttribute("totalRevenue", reportService.getTotalRevenue());

        req.getRequestDispatcher("/views/reports.jsp").forward(req, resp);
    }
}
