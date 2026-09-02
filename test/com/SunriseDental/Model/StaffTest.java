package com.SunriseDental.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StaffTest {

    @Test
    void testIsAdmin_TrueForAdminRole() {
        Staff s = new Staff();
        s.setRole("ADMIN");
        assertTrue(s.isAdmin());
    }

    @Test
    void testIsAdmin_FalseForGuestRole() {
        Staff s = new Staff();
        s.setRole("GUEST");
        assertFalse(s.isAdmin());
    }

    @Test
    void testIsAdmin_CaseInsensitive() {
        Staff s = new Staff();
        s.setRole("admin");
        assertTrue(s.isAdmin(), "Role check should be case-insensitive");
    }

    @Test
    void testSettersAndGetters() {
        Staff s = new Staff();
        s.setStaffId(1);
        s.setUsername("admin");
        s.setPassword("admin123");
        s.setFullName("Clinic Administrator");
        s.setEmail("admin@sunrisedental.example");
        s.setRole("ADMIN");
        s.setStatus("ACTIVE");

        assertEquals(1, s.getStaffId());
        assertEquals("admin", s.getUsername());
        assertEquals("admin123", s.getPassword());
        assertEquals("Clinic Administrator", s.getFullName());
        assertEquals("admin@sunrisedental.example", s.getEmail());
        assertEquals("ADMIN", s.getRole());
        assertEquals("ACTIVE", s.getStatus());
    }
}
