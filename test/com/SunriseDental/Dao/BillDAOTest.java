package com.SunriseDental.Dao;

import com.SunriseDental.Model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BillDAOTest {

    private BillDAO billDAO;
    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() {
        billDAO = new BillDAO();
        appointmentDAO = new AppointmentDAO();
    }

    @Test
    void testFindByAppointmentNumber_NonExistentBill() {
        Bill found = billDAO.findByAppointmentNumber("APT9999");
        assertNull(found, "Should return null when no bill exists for that appointment yet");
    }

    @Test
    void testSaveAndFindBill() {
        // Uses a fresh, not-yet-billed appointment number so this test is
        // independent of whatever appointments already exist in the DB.
        String freshNumber = appointmentDAO.getNextAppointmentNumber();
        Bill bill = new Bill(freshNumber, 1500.00);

        boolean saved = billDAO.save(bill);
        assertTrue(saved, "Bill should be saved successfully");

        Bill found = billDAO.findByAppointmentNumber(freshNumber);
        assertNotNull(found);
        assertEquals(1500.00, found.getTotalAmount(), 0.01);
    }
}
