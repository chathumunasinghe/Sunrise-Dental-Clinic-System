package com.SunriseDental.Dao;

import com.SunriseDental.Model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Requires a running MySQL instance with the sunrise_dental schema loaded
 * (see database/schema.sql), matching DBConnection's settings.
 */
public class PatientDAOTest {

    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() {
        patientDAO = new PatientDAO();
    }

    @Test
    void testSaveAndFindPatient() {
        String newId = patientDAO.getNextPatientId();
        Patient patient = new Patient(newId, "Test Patient", "Colombo", "0770000000", "test@example.com");

        boolean saved = patientDAO.save(patient);
        assertTrue(saved, "Patient should be saved successfully");

        Patient found = patientDAO.findById(newId);
        assertNotNull(found, "Saved patient should be retrievable");
        assertEquals("Test Patient", found.getName());
        assertEquals("0770000000", found.getContactNumber());
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void testFindById_NonExistentPatient() {
        Patient found = patientDAO.findById("PT9999");
        assertNull(found, "Should return null for a patient ID that doesn't exist");
    }

    @Test
    void testGetNextPatientId_FormatIsCorrect() {
        String nextId = patientDAO.getNextPatientId();
        assertTrue(nextId.matches("PT\\d{4}"), "Patient ID should follow the PTxxxx format");
    }

    @Test
    void testGetPatientDirectory_ReturnsAList() {
        List<Map<String, Object>> directory = patientDAO.getPatientDirectory();
        assertNotNull(directory, "Directory should never be null, even when empty");
    }
}
