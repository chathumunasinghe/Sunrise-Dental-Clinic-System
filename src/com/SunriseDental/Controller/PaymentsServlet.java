package com.SunriseDental.Controller;

import com.SunriseDental.Model.Staff;
import com.SunriseDental.Service.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Front-desk (and admin) view of every patient's payment status — unlike
 * Billing, which needs an exact appointment number, this is a browsable
 * list so reception can see at a glance who has paid and who still owes,
 * and jump straight into a bill to collect/record a cash payment.
 */
@WebServlet("/payments")
public class PaymentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Staff staff = session == null ? null : (Staff) session.getAttribute("staff");
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        // Dentists don't handle billing — everyone else (admin, front desk) can.
        if (staff.isDentist()) {
            resp.sendRedirect(req.getContextPath() + "/dentistDashboard");
            return;
        }

        String search = req.getParameter("search");
        String filter = req.getParameter("filter"); // "all" | "paid" | "unpaid"
        if (filter == null || filter.isBlank()) filter = "all";

        ReportService reportService = new ReportService();
        req.setAttribute("bills", reportService.getAllBills(search));
        req.setAttribute("search", search == null ? "" : search);
        req.setAttribute("filter", filter);

        req.getRequestDispatcher("/views/payments.jsp").forward(req, resp);
    }
}
