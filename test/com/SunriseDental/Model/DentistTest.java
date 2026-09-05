package com.SunriseDental.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DentistTest {

    @Test
    void testNoArgConstructor() {
        Dentist d = new Dentist();
        assertEquals(0, d.getDentistId());
        assertNull(d.getName());
        assertNull(d.getSpecialization());
    }

    @Test
    void testBasicConstructor() {
        Dentist d = new Dentist(1, "Dr. Silva", "Orthodontics");

        assertEquals(1, d.getDentistId());
        assertEquals("Dr. Silva", d.getName());
        assertEquals("Orthodontics", d.getSpecialization());
        assertNull(d.getQualification());
        assertNull(d.getExperienceYears());
    }

    @Test
    void testFullConstructor() {
        Dentist d = new Dentist(2, "Dr. Perera", "Endodontics", "BDS, MSc",
                8, "Root canal specialist", "perera@sunrisedental.example",
                "Mon-Fri", "ACTIVE");

        assertEquals(2, d.getDentistId());
        assertEquals("Dr. Perera", d.getName());
        assertEquals("Endodontics", d.getSpecialization());
        assertEquals("BDS, MSc", d.getQualification());
        assertEquals(8, d.getExperienceYears());
        assertEquals("Root canal specialist", d.getBio());
        assertEquals("perera@sunrisedental.example", d.getEmail());
        assertEquals("Mon-Fri", d.getConsultationDays());
        assertEquals("ACTIVE", d.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        Dentist d = new Dentist();
        d.setDentistId(3);
        d.setName("Dr. Fernando");
        d.setSpecialization("Pediatric Dentistry");
        d.setQualification("BDS");
        d.setExperienceYears(5);
        d.setBio("Specializes in child dental care");
        d.setEmail("fernando@sunrisedental.example");
        d.setConsultationDays("Tue, Thu, Sat");
        d.setStatus("DISABLED");

        assertEquals(3, d.getDentistId());
        assertEquals("Dr. Fernando", d.getName());
        assertEquals("Pediatric Dentistry", d.getSpecialization());
        assertEquals("BDS", d.getQualification());
        assertEquals(5, d.getExperienceYears());
        assertEquals("Specializes in child dental care", d.getBio());
        assertEquals("fernando@sunrisedental.example", d.getEmail());
        assertEquals("Tue, Thu, Sat", d.getConsultationDays());
        assertEquals("DISABLED", d.getStatus());
    }

    @Test
    void testIsActive_TrueForActiveStatus() {
        Dentist d = new Dentist();
        d.setStatus("ACTIVE");
        assertTrue(d.isActive());
    }

    @Test
    void testIsActive_TrueForNullStatus() {
        Dentist d = new Dentist();
        d.setStatus(null);
        assertTrue(d.isActive(), "Null status should default to active");
    }

    @Test
    void testIsActive_CaseInsensitive() {
        Dentist d = new Dentist();
        d.setStatus("active");
        assertTrue(d.isActive());
    }

    @Test
    void testIsActive_FalseForDisabledStatus() {
        Dentist d = new Dentist();
        d.setStatus("DISABLED");
        assertFalse(d.isActive());
    }
}
