package com.SunriseDental.Service;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.NotificationDAO;
import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;
import com.SunriseDental.Service.Notification.NotificationService;

import java.security.SecureRandom;
import java.sql.Date;

public class AppointmentService {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private NotificationService notificationService = new NotificationService();
    private NotificationDAO notificationDAO = new NotificationDAO();

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    /**
     * Result of a front-desk appointment registration: the appointment
     * number plus (when a brand-new patient record had to be created)
     * the portal login the front desk should hand to the patient so they
     * can see the appointment themselves under "My Appointments" instead
     * of only ever receiving an outgoing email/SMS notification about it.
     */
    public static class RegistrationResult {
        public final String appointmentNumber;
        public final String patientId;
        public final boolean matchedExistingPatient;
        public final boolean alreadyHadPortalLogin;
        public final String generatedUsername;  // null unless a new login was created
        public final String generatedPassword;  // null unless a new login was created

        RegistrationResult(String appointmentNumber, String patientId, boolean matchedExistingPatient,
                            boolean alreadyHadPortalLogin, String generatedUsername, String generatedPassword) {
            this.appointmentNumber = appointmentNumber;
            this.patientId = patientId;
            this.matchedExistingPatient = matchedExistingPatient;
            this.alreadyHadPortalLogin = alreadyHadPortalLogin;
            this.generatedUsername = generatedUsername;
            this.generatedPassword = generatedPassword;
        }
    }

    /**
     * Registers a new appointment from the front desk. Backward-compatible
     * wrapper around {@link #registerAppointmentDetailed} that just returns
     * the appointment number (used by existing callers/tests).
     */
    public String registerAppointment(String patientId, String name, String address, String contact,
                                       String email, int dentistId, int treatmentId, Date date, String time) {
        RegistrationResult result = registerAppointmentDetailed(
                patientId, name, address, contact, email, dentistId, treatmentId, date, time);
        return result == null ? null : result.appointmentNumber;
    }

    /**
     * Registers a new appointment taken by the front desk (walk-in or phone
     * booking). This is the flow the earlier version of this app got wrong:
     * if staff didn't type the patient's exact ID, a brand-new, login-less
     * patient record was created every time — so the appointment would
     * never appear under that patient's own "My Appointments" when they
     * signed in, only ever reaching them as an outgoing email/SMS
     * notification. This version fixes that in two ways:
     *
     * 1. If "Existing Patient ID" is left blank, we still try to match the
     *    person to an existing account by contact number before creating a
     *    new record, so a returning patient's booking lands on the same
     *    account they log in with.
     * 2. If it really is a brand-new patient, we generate a portal
     *    username/password for them immediately (instead of leaving those
     *    columns empty) so they can log in and see the appointment right
     *    away. The generated credentials are returned here so the front
     *    desk can hand them to the patient.
     */
    public RegistrationResult registerAppointmentDetailed(String patientId, String name, String address, String contact,
                                                            String email, int dentistId, int treatmentId, Date date, String time) {

        if (name == null || name.trim().isEmpty() || contact == null || contact.trim().isEmpty()) {
            return null; // basic validation
        }

        Patient existing = (patientId != null && !patientId.isEmpty()) ? patientDAO.findById(patientId) : null;
        if (existing == null) {
            // No exact ID given (or it didn't match) — fall back to matching
            // by contact number so we don't create a duplicate, login-less
            // record for someone who already has a portal account.
            existing = patientDAO.findByContact(contact);
        }

        Patient patientRecord;
        boolean matchedExisting = existing != null;
        boolean alreadyHadLogin;
        String generatedUsername = null;
        String generatedPassword = null;

        if (matchedExisting) {
            patientRecord = existing;
            alreadyHadLogin = patientRecord.getUsername() != null && !patientRecord.getUsername().isBlank();
        } else {
            String newPatientId = patientDAO.getNextPatientId();
            patientRecord = new Patient(newPatientId, name, address, contact, email);

            generatedUsername = generateUsername(contact, newPatientId);
            generatedPassword = generateTempPassword();
            patientRecord.setUsername(generatedUsername);
            patientRecord.setPassword(generatedPassword);

            patientDAO.save(patientRecord);
            alreadyHadLogin = false;
        }

        String appointmentNumber = appointmentDAO.getNextAppointmentNumber();

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatientId(patientRecord.getPatientId());
        appointment.setDentistId(dentistId);
        appointment.setTreatmentId(treatmentId);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus("Scheduled");

        boolean saved = appointmentDAO.save(appointment);
        if (!saved) {
            return null;
        }

        notificationService.sendAppointmentConfirmation(patientRecord, appointment, appointmentNumber);
        String notifyMsg = "Your appointment " + appointmentNumber + " has been booked for " + date + " at " + time + ".";
        if (generatedUsername != null) {
            notifyMsg += " Portal login — username: " + generatedUsername + ", password: " + generatedPassword
                    + " (please change it after logging in).";
        }
        notificationDAO.create(patientRecord.getPatientId(), appointmentNumber, notifyMsg);

        return new RegistrationResult(appointmentNumber, patientRecord.getPatientId(), matchedExisting,
                alreadyHadLogin, generatedUsername, generatedPassword);
    }

    private String generateUsername(String contact, String fallbackPatientId) {
        String digitsOnly = contact.replaceAll("[^0-9]", "");
        String base = digitsOnly.isBlank() ? fallbackPatientId.toLowerCase() : digitsOnly;
        String candidate = base;
        int suffix = 1;
        while (patientDAO.usernameExists(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    public Appointment searchByNumber(String appointmentNumber) {
        return appointmentDAO.findByNumber(appointmentNumber);
    }

    /**
     * Books an appointment for a patient who is already registered and
     * signed in through the patient portal — no new patient record is
     * created, unlike registerAppointment() above which is used by the
     * front desk for walk-in/phone bookings.
     */
    public String bookForPatient(String patientId, int dentistId, int treatmentId, Date date, String time) {
        Patient patientRecord = patientDAO.findById(patientId);
        if (patientRecord == null) {
            return null;
        }

        String appointmentNumber = appointmentDAO.getNextAppointmentNumber();

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatientId(patientId);
        appointment.setDentistId(dentistId);
        appointment.setTreatmentId(treatmentId);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus("Scheduled");

        boolean saved = appointmentDAO.save(appointment);
        if (!saved) {
            return null;
        }

        notificationService.sendAppointmentConfirmation(patientRecord, appointment, appointmentNumber);
        notificationDAO.create(patientId, appointmentNumber,
                "Your appointment " + appointmentNumber + " has been confirmed for " + date + " at " + time + ".");
        return appointmentNumber;
    }
}
