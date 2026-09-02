package com.SunriseDental.Dao;

import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentDAOTest {

    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
    }

    @Test
    void testGetNextAppointmentNumber_FormatIsCorrect() {
        String nextNumber = appointmentDAO.getNextAppointmentNumber();
        assertTrue(nextNumber.matches("APT\\d{4}"), "Appointment number should follow the APTxxxx format");
    }

    @Test
    void testSaveAndFindAppointment() {
        // Arrange: create a patient first, since appointments have a FK to patients
        String patientId = patientDAO.getNextPatientId();
        patientDAO.save(new Patient(patientId, "Bill Test Patient", "Galle", "0711112222"));

        String appointmentNumber = appointmentDAO.getNextAppointmentNumber();
        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatientId(patientId);
        appointment.setDentistId(1);
        appointment.setTreatmentId(1);
        appointment.setAppointmentDate(Date.valueOf("2026-09-10"));
        appointment.setAppointmentTime("09:00");
        appointment.setStatus("Scheduled");

        boolean saved = appointmentDAO.save(appointment);

        assertTrue(saved, "Appointment should be saved successfully");
        Appointment found = appointmentDAO.findByNumber(appointmentNumber);
        assertNotNull(found);
        assertEquals(patientId, found.getPatientId());
        assertEquals("Scheduled", found.getStatus());
    }

    @Test
    void testFindByNumber_NonExistentAppointment() {
        Appointment found = appointmentDAO.findByNumber("APT9999");
        assertNull(found, "Should return null for a non-existent appointment number");
    }

    @Test
    void testGetFullHistory_ReturnsAList() {
        List<Map<String, Object>> history = appointmentDAO.getFullHistory();
        assertNotNull(history, "History should never be null, even when empty");
    }
}
