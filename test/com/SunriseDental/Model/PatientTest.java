package com.SunriseDental.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {

    @Test
    void testFourArgConstructorAndGetters() {
        Patient p = new Patient("PT0001", "Nimal Perera", "Colombo 05", "0771234567");
        assertEquals("PT0001", p.getPatientId());
        assertEquals("Nimal Perera", p.getName());
        assertEquals("Colombo 05", p.getAddress());
        assertEquals("0771234567", p.getContactNumber());
        assertNull(p.getEmail(), "Email should be null when not supplied");
    }

    @Test
    void testFiveArgConstructorIncludesEmail() {
        Patient p = new Patient("PT0002", "Kamal Silva", "Nugegoda", "0719876543", "kamal@example.com");
        assertEquals("kamal@example.com", p.getEmail());
    }

    @Test
    void testSettersAndGetters() {
        Patient p = new Patient();
        p.setPatientId("PT0003");
        p.setName("Sanduni Fernando");
        p.setAddress("Galle");
        p.setContactNumber("0712223333");
        p.setEmail("sanduni@example.com");

        assertEquals("PT0003", p.getPatientId());
        assertEquals("Sanduni Fernando", p.getName());
        assertEquals("Galle", p.getAddress());
        assertEquals("0712223333", p.getContactNumber());
        assertEquals("sanduni@example.com", p.getEmail());
    }
}
