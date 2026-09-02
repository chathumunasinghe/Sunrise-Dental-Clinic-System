package com.SunriseDental.Filter;

import com.SunriseDental.Model.Staff;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Guards every page under /views/ except the public auth pages
 * (login, forgot password, reset password). Also enforces that
 * admin-only screens (reports, staff management) are blocked for
 * the GUEST role, redirecting them back to the dashboard instead.
 */
@WebFilter(urlPatterns = {"/views/*"})
public class AuthFilter implements Filter {

    private static final String[] PUBLIC_PAGES = {
        "login.jsp", "forgotPassword.jsp", "resetPassword.jsp"
    };

    private static final String[] ADMIN_ONLY_PAGES = {
        "reports.jsp", "manageStaff.jsp", "managePatients.jsp"
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
