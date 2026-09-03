package com.SunriseDental.Controller;

import com.SunriseDental.Dao.DentistDAO;
import com.SunriseDental.Model.Dentist;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Patient-facing detail page for a single doctor — qualifications, experience, bio, schedule. */
@WebServlet("/doctorProfile")
public class DoctorProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        int dentistId;
        try {
            dentistId = Integer.parseInt(req.getParameter("dentistId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/doctors");
            return;
        }

        Dentist dentist = new DentistDAO().findById(dentistId);
        if (dentist == null) {
            resp.sendRedirect(req.getContextPath() + "/doctors");
            return;
        }

        req.setAttribute("dentist", dentist);
        req.getRequestDispatcher("/views/doctorProfile.jsp").forward(req, resp);
    }
}
