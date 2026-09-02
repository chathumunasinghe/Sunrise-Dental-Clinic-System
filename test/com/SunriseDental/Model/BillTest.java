package com.SunriseDental.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BillTest {

    @Test
    void testTwoArgConstructor() {
        Bill bill = new Bill("APT0001", 4500.00);
        assertEquals("APT0001", bill.getAppointmentNumber());
        assertEquals(4500.00, bill.getTotalAmount());
    }

    @Test
    void testSettersAndGetters() {
        Bill bill = new Bill();
        bill.setBillId(1);
        bill.setAppointmentNumber("APT0002");
        bill.setTotalAmount(1500.00);
        bill.setIssueDate("2026-09-01 10:30:00");

        assertEquals(1, bill.getBillId());
        assertEquals("APT0002", bill.getAppointmentNumber());
        assertEquals(1500.00, bill.getTotalAmount());
        assertEquals("2026-09-01 10:30:00", bill.getIssueDate());
    }
}
