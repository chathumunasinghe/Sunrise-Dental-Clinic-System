package com.SunriseDental.Service;

import com.SunriseDental.Model.Bill;
import org.junit.jupiter.api.Test;
import java.sql.Date;
import static org.junit.jupiter.api.Assertions.*;

public class BillingServiceTest {

    private BillingService billingService = new BillingService();
    private AppointmentService appointmentService = new AppointmentService();

    @Test
    void testGenerateBill_ForValidAppointment_CalculatesCorrectFee() {
        String appointmentNumber = appointmentService.registerAppointment(
                null, "Billing Test Patient", "Kandy", "0778889900", "billingtest@example.com",
                1, 1, Date.valueOf("2026-09-20"), "14:00");
        assertNotNull(appointmentNumber);

        Bill bill = billingService.generateBill(appointmentNumber);

        assertNotNull(bill, "Bill should be generated for a valid appointment");
        assertEquals(appointmentNumber, bill.getAppointmentNumber());
        assertEquals(1500.00, bill.getTotalAmount(), 0.01, "Fee should match Consultation's fee");
    }

    @Test
    void testGenerateBill_CalledTwice_DoesNotDuplicate() {
        String appointmentNumber = appointmentService.registerAppointment(
                null, "No Duplicate Bill Patient", "Kandy", "0778889901", "noduplicate@example.com",
                1, 1, Date.valueOf("2026-09-21"), "15:00");
        assertNotNull(appointmentNumber);

        Bill firstCall = billingService.generateBill(appointmentNumber);
        Bill secondCall = billingService.generateBill(appointmentNumber);

        assertNotNull(firstCall);
        assertNotNull(secondCall);
        assertEquals(firstCall.getBillId(), secondCall.getBillId(),
                "Calling generateBill twice for the same appointment must return the existing bill, not create a duplicate row");
    }

    @Test
    void testGenerateBill_ForNonExistentAppointment_ReturnsNull() {
        Bill bill = billingService.generateBill("APT9999");
        assertNull(bill, "Should return null when the appointment does not exist");
    }
}
