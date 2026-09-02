package com.SunriseDental.Controller;

import com.SunriseDental.Model.Bill;
import com.SunriseDental.Service.BillingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/billing")
public class BillingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (req.getSession().getAttribute("staff") == null) {
            resp.sendRedirect("login");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        if (appointmentNumber != null && !appointmentNumber.trim().isEmpty()) {
            BillingService billingService = new BillingService();
            Bill bill = billingService.generateBill(appointmentNumber.trim());

            if (bill != null) {
                req.setAttribute("bill", bill);
                req.setAttribute("receipt", billingService.getReceiptDetails(appointmentNumber.trim()));
            } else {
                req.setAttribute("error", "Could not generate bill. Check the appointment number.");
            }
        }
        req.getRequestDispatcher("/views/billing.jsp").forward(req, resp);
    }
}
