package com.SunriseDental.Controller;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Lets the assigned dentist save clinical treatment notes for one of their
 * own appointments and mark it Completed. An ownership check (the
 * appointment's dentist_id must match the logged-in dentist's own
 * dentist_id) stops a dentist from editing another dentist's patient notes
 * just by changing the appointmentNumber in the form.
 */
@WebServlet("/updateTreatmentNotes")
public class UpdateTreatmentNotesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Staff staff = (session != null) ? (Staff) session.getAttribute("staff") : null;

        if (staff == null || !staff.isDentist() || staff.getDentistId() == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        String notes = req.getParameter("notes");
        boolean markCompleted = "1".equals(req.getParameter("markCompleted"));

        AppointmentDAO appointmentDAO = new AppointmentDAO();
        Appointment appointment = appointmentDAO.findByNumber(appointmentNumber);

        if (appointment != null && appointment.getDentistId() == staff.getDentistId()) {
            boolean hadNoNotesBefore = appointment.getTreatmentNotes() == null || appointment.getTreatmentNotes().isBlank();
            boolean hasNotesNow = notes != null && !notes.isBlank();
            appointmentDAO.updateTreatmentNotes(appointmentNumber, notes);

            if (markCompleted) {
                appointmentDAO.updateStatus(appointmentNumber, "Completed");
                new com.SunriseDental.Dao.NotificationDAO().create(appointment.getPatientId(), appointmentNumber,
                        "Your appointment " + appointmentNumber + " is now marked Completed by your dentist.");
            }
            // Let the patient know a note was added/updated so they check
            // "My Appointments" for it, even when the visit isn't being
            // marked Completed in this same action.
            if (hasNotesNow && (hadNoNotesBefore || !markCompleted)) {
                new com.SunriseDental.Dao.NotificationDAO().create(appointment.getPatientId(), appointmentNumber,
                        "Your dentist added a note to appointment " + appointmentNumber + ". Check My Appointments to view it.");
            }
        }

        resp.sendRedirect(req.getContextPath() + "/dentistDashboard");
    }
}
