package com.SunriseDental.Dao;

import com.SunriseDental.Model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class StaffDAOTest {

    private StaffDAO staffDAO;

    @BeforeEach
    void setUp() {
        staffDAO = new StaffDAO();
    }

    @Test
    void testValidateLogin_ValidCredentials() {
        // Seeded in schema.sql: username=admin, password=admin123, status=ACTIVE
        Staff staff = staffDAO.validateLogin("admin", "admin123");
        assertNotNull(staff, "Valid credentials for an active account should return a Staff object");
        assertEquals("admin", staff.getUsername());
        assertTrue(staff.isAdmin());
    }

    @Test
    void testValidateLogin_InvalidPassword() {
        Staff staff = staffDAO.validateLogin("admin", "wrongpassword");
        assertNull(staff, "Invalid credentials should return null");
    }

    @Test
    void testValidateLogin_NonExistentUser() {
        Staff staff = staffDAO.validateLogin("ghost", "whatever");
        assertNull(staff, "Non-existent username should return null");
    }

    @Test
    void testFindByUsernameOrEmail_MatchesByUsername() {
        Staff staff = staffDAO.findByUsernameOrEmail("admin");
        assertNotNull(staff);
        assertEquals("admin", staff.getUsername());
    }

    @Test
    void testFindByUsernameOrEmail_MatchesByEmail() {
        Staff byUsername = staffDAO.findByUsernameOrEmail("admin");
        assertNotNull(byUsername, "Precondition: admin account must exist");

        Staff byEmail = staffDAO.findByUsernameOrEmail(byUsername.getEmail());
        assertNotNull(byEmail, "Should also find the account by its email address");
        assertEquals("admin", byEmail.getUsername());
    }

    @Test
    void testFindByUsernameOrEmail_Unknown() {
        Staff staff = staffDAO.findByUsernameOrEmail("nobody@nowhere.example");
        assertNull(staff);
    }

    @Test
    void testPasswordResetFlow_TokenLifecycle() {
        Staff admin = staffDAO.validateLogin("admin", "admin123");
        assertNotNull(admin, "Precondition: admin login must work before testing reset flow");

        // 1. Generate a token
        String token = staffDAO.createPasswordResetToken(admin.getStaffId());
        assertNotNull(token, "A reset token should be generated");

        // 2. Token should validate to the correct staff ID
        int resolvedStaffId = staffDAO.validateResetToken(token);
        assertEquals(admin.getStaffId(), resolvedStaffId, "Token should resolve back to the same staff member");

        // 3. An invalid/unknown token should not validate
        assertEquals(-1, staffDAO.validateResetToken("not-a-real-token"));

        // 4. Using the token to reset the password should succeed
        boolean resetOk = staffDAO.resetPassword(token, admin.getStaffId(), "admin123");
        assertTrue(resetOk, "Password reset should succeed with a valid token");
    }

    @Test
    void testAddStaff_And_GetAllStaff() {
        String uniqueUsername = "testuser" + System.currentTimeMillis();
        boolean added = staffDAO.addStaff(uniqueUsername, "temp123", "Temp Test User",
                uniqueUsername + "@example.com", "GUEST");
        assertTrue(added, "New staff account should be created successfully");

        List<Staff> all = staffDAO.getAllStaff();
        boolean found = all.stream().anyMatch(s -> uniqueUsername.equals(s.getUsername()));
        assertTrue(found, "Newly added staff member should appear in getAllStaff()");
    }

    @Test
    void testSetStaffStatus_DisablesAccountLogin() {
        // Create a throwaway account, disable it, then confirm login fails.
        String uniqueUsername = "disabletest" + System.currentTimeMillis();
        staffDAO.addStaff(uniqueUsername, "temp123", "Disable Test", uniqueUsername + "@example.com", "GUEST");

        Staff created = staffDAO.findByUsernameOrEmail(uniqueUsername);
        assertNotNull(created, "Precondition: account must exist before disabling it");

        boolean updated = staffDAO.setStaffStatus(created.getStaffId(), "DISABLED");
        assertTrue(updated);

        Staff loginAttempt = staffDAO.validateLogin(uniqueUsername, "temp123");
        assertNull(loginAttempt, "A disabled account must not be able to log in");
    }
}
