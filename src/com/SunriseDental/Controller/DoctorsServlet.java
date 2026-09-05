package com.SunriseDental.Controller;

import com.SunriseDental.Dao.DentistDAO;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Patient-facing "Meet our Doctors" listing, the entry point for booking an appointment. */
@WebServlet("/doctors")
public class DoctorsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        DentistDAO dentistDAO = new DentistDAO();
        req.setAttribute("dentists", dentistDAO.getActiveDentists());
        req.getRequestDispatcher("/views/doctors.jsp").forward(req, resp);
    }
}
