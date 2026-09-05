package com.SunriseDental.Controller;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Service.AppointmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/searchAppointment")
public class SearchAppointmentServlet extends HttpServlet {

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
            AppointmentService service = new AppointmentService();
            Appointment appointment = service.searchByNumber(appointmentNumber.trim());

            if (appointment != null) {
                req.setAttribute("appointment", appointment);
            } else {
                req.setAttribute("error", "No appointment found with number: " + appointmentNumber);
            }
        }
        req.getRequestDispatcher("/views/searchAppointment.jsp").forward(req, resp);
    }
}
