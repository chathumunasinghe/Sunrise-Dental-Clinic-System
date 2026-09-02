package com.SunriseDental.Model;

import org.junit.jupiter.api.Test;
import java.sql.Date;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Test
    void testSettersAndGetters() {
        Appointment a = new Appointment();
        a.setAppointmentNumber("APT0001");
        a.setPatientId("PT0001");
        a.setDentistId(1);
        a.setTreatmentId(2);
        a.setAppointmentDate(Date.valueOf("2026-09-01"));
        a.setAppointmentTime("10:30");
        a.setStatus("Scheduled");

        assertEquals("APT0001", a.getAppointmentNumber());
        assertEquals("PT0001", a.getPatientId());
        assertEquals(1, a.getDentistId());
        assertEquals(2, a.getTreatmentId());
        assertEquals(Date.valueOf("2026-09-01"), a.getAppointmentDate());
        assertEquals("10:30", a.getAppointmentTime());
        assertEquals("Scheduled", a.getStatus());
    }
}
