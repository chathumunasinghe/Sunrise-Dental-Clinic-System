package com.SunriseDental.Filter;

import com.SunriseDental.Model.Patient;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Guards every page under /views/ except the public auth pages (staff
 * login/forgot/reset and the patient portal's signup/forgot/reset).
 * Also enforces that admin-only staff screens (reports, staff management)
 * are blocked for the GUEST role, and that patient-portal screens require
 * a signed-in patient rather than a staff session.
 */
@WebFilter(urlPatterns = {"/views/*"})
public class AuthFilter implements Filter {

    private static final String[] PUBLIC_PAGES = {
        "login.jsp", "forgotPassword.jsp", "resetPassword.jsp",
        "patientSignup.jsp", "patientForgotPassword.jsp", "patientResetPassword.jsp"
    };

    private static final String[] ADMIN_ONLY_PAGES = {
        "reports.jsp", "manageStaff.jsp", "managePatients.jsp"
    };

    /** Pages meant for a signed-in patient, not staff. */
    private static final String[] PATIENT_ONLY_PAGES = {
        "patientDashboard.jsp", "doctors.jsp", "bookAppointment.jsp", "payment.jsp"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String page = uri.substring(uri.lastIndexOf('/') + 1);

        if (isOneOf(page, PUBLIC_PAGES)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (isOneOf(page, PATIENT_ONLY_PAGES)) {
            Patient patient = (session != null) ? (Patient) session.getAttribute("patient") : null;
            if (patient == null) {
                resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        Staff staff = (session != null) ? (Staff) session.getAttribute("staff") : null;

        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        if (isOneOf(page, ADMIN_ONLY_PAGES) && !staff.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isOneOf(String page, String[] list) {
        for (String p : list) {
            if (p.equalsIgnoreCase(page)) return true;
        }
        return false;
    }
}
