package com.SunriseDental.Controller;

import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Admin-only action: permanently deletes a patient (and their appointment
 * and billing history) from the "Patients" screen. AuthFilter already
 * blocks non-admin staff from the managePatients.jsp view, but the role
 * is re-checked here too since this servlet can be hit directly.
 */
@WebServlet("/deletePatient")
public class DeletePatientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String patientId = req.getParameter("patientId");
        String redirect = req.getContextPath() + "/managePatients";

        if (patientId == null || patientId.isBlank()) {
            resp.sendRedirect(redirect + "?error=" + urlEncode("Please choose a patient to delete."));
            return;
        }

        boolean deleted = new PatientDAO().deletePatientCascade(patientId.trim());
        if (deleted) {
            resp.sendRedirect(redirect + "?message=" + urlEncode("Patient " + patientId + " and their records were deleted."));
        } else {
            resp.sendRedirect(redirect + "?error=" + urlEncode("Could not delete that patient. Please try again."));
        }
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return s;
        }
    }
}
