package com.SunriseDental.Service;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.PatientDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;
import com.SunriseDental.Service.Notification.NotificationService;

import java.sql.Date;

public class AppointmentService {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private PatientDAO patientDAO = new PatientDAO();
    private NotificationService notificationService = new NotificationService();

    /**
     * Registers a new appointment. If the patient does not already exist
     * (identified by patient ID), a new patient record is created first.
     * On success, an appointment confirmation is sent through every active
     * notification channel (email, SMS). Returns the generated appointment
     * number, or null on validation failure.
     */
    public String registerAppointment(String patientId, String name, String address, String contact,
                                       String email, int dentistId, int treatmentId, Date date, String time) {

        if (name == null || name.trim().isEmpty() || contact == null || contact.trim().isEmpty()) {
            return null; // basic validation
        }

        Patient existing = (patientId != null && !patientId.isEmpty()) ? patientDAO.findById(patientId) : null;

        Patient patientRecord;
        if (existing != null) {
            patientRecord = existing;
        } else {
            String newPatientId = patientDAO.getNextPatientId();
            patientRecord = new Patient(newPatientId, name, address, contact, email);
            patientDAO.save(patientRecord);
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
        return appointmentNumber;
    }

    public Appointment searchByNumber(String appointmentNumber) {
        return appointmentDAO.findByNumber(appointmentNumber);
    }
}
