package com.SunriseDental.Controller;

import com.SunriseDental.Dao.DentistDAO;
import com.SunriseDental.Model.Dentist;
import com.SunriseDental.Model.Patient;
import com.SunriseDental.Service.AvailabilityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

/**
 * AJAX endpoint behind the "Appointment Time" field on the patient booking
 * page: given a doctor and a date, returns that doctor's real open slots
 * for the day (their published hours minus whatever's already booked and
 * whatever's already in the past) as JSON, so the patient only ever sees
 * times that can actually be booked instead of typing in any time at all.
 */
@WebServlet("/availableSlots")
public class AvailableSlotsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Patient patient = session == null ? null : (Patient) session.getAttribute("patient");
        if (patient == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, false, Collections.emptyList(), "Please log in first.");
            return;
        }

        int dentistId;
        LocalDate date;
        try {
            dentistId = Integer.parseInt(req.getParameter("dentistId"));
            date = LocalDate.parse(req.getParameter("date"));
        } catch (NumberFormatException | DateTimeParseException | NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, false, Collections.emptyList(), "Please choose a doctor and date.");
            return;
        }

        Dentist dentist = new DentistDAO().findById(dentistId);
        if (dentist == null || !dentist.isActive()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(resp, false, Collections.emptyList(), "This doctor is not currently available.");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            writeJson(resp, false, Collections.emptyList(), "That date has already passed.");
            return;
        }

        AvailabilityService.DayAvailability availability = new AvailabilityService().getAvailability(dentist, date);
        if (!availability.workingDay) {
            writeJson(resp, false, availability.slots, dentist.getName() + " does not consult on this day.");
        } else if (availability.slots.isEmpty()) {
            writeJson(resp, true, availability.slots, "All appointments for this day are fully booked.");
        } else {
            writeJson(resp, true, availability.slots, null);
        }
    }

    private void writeJson(HttpServletResponse resp, boolean workingDay, List<String> slots, String message)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"workingDay\":").append(workingDay).append(",\"slots\":[");
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(slots.get(i)).append("\"");
        }
        sb.append("]");
        if (message != null) {
            sb.append(",\"message\":\"").append(escapeJson(message)).append("\"");
        }
        sb.append("}");
        PrintWriter out = resp.getWriter();
        out.write(sb.toString());
        out.flush();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "'");
    }
}
