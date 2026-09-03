package com.SunriseDental.Controller;

import com.SunriseDental.Dao.TreatmentTypeDAO;
import com.SunriseDental.Model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Patient-facing "Treatments & Prices" page — lets a patient see the
 * clinic's treatment list and consultation fees before booking, instead
 * of only discovering the price once they're on the booking form.
 */
@WebServlet("/treatments")
public class TreatmentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        req.setAttribute("treatmentTypes", new TreatmentTypeDAO().getAllTreatmentTypes());
        req.getRequestDispatcher("/views/treatments.jsp").forward(req, resp);
    }
}
