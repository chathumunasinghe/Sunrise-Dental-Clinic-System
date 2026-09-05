package com.SunriseDental.Service;

import org.junit.jupiter.api.Test;
import java.sql.Date;
import static org.junit.jupiter.api.Assertions.*;

public class AppointmentServiceTest {

    private AppointmentService service = new AppointmentService();

    @Test
    void testRegisterAppointment_NewPatient_Succeeds() {
        String appointmentNumber = service.registerAppointment(
                null, "New Patient Test", "Colombo", "0765554433", "newpatient@example.com",
                1, 1, Date.valueOf("2026-09-15"), "11:00");

        assertNotNull(appointmentNumber, "A new appointment number should be returned on success");
        assertTrue(appointmentNumber.matches("APT\\d{4}"));
    }

    @Test
    void testRegisterAppointment_MissingRequiredFields_ReturnsNull() {
        // name and contact are mandatory per validation rules
        String result = service.registerAppointment(
                null, "", "Colombo", "", "",
                1, 1, Date.valueOf("2026-09-15"), "11:00");

        assertNull(result, "Registration should fail when name/contact are missing");
    }

    @Test
    void testSearchByNumber_NonExistentAppointment_ReturnsNull() {
        assertNull(service.searchByNumber("APT9999"));
    }
}
