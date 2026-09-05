package com.SunriseDental.Service;

import com.SunriseDental.Dao.AppointmentDAO;
import com.SunriseDental.Dao.BillDAO;
import com.SunriseDental.Dao.TreatmentTypeDAO;
import com.SunriseDental.Model.Appointment;
import com.SunriseDental.Model.Bill;

import java.util.Map;

public class BillingService {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private TreatmentTypeDAO treatmentTypeDAO = new TreatmentTypeDAO();
    private BillDAO billDAO = new BillDAO();

    /**
     * Calculates and persists the bill for a given appointment based on the
     * consultation fee of its treatment type. Returns the Bill, or null if
     * the appointment doesn't exist.
     */
    public Bill generateBill(String appointmentNumber) {
        Appointment appointment = appointmentDAO.findByNumber(appointmentNumber);
        if (appointment == null) {
            return null;
        }

        Bill existing = billDAO.findByAppointmentNumber(appointmentNumber);
        if (existing != null) {
            return existing; // already billed — don't create a duplicate row
        }

        double fee = treatmentTypeDAO.getFee(appointment.getTreatmentId());
        Bill bill = new Bill(appointmentNumber, fee);
        billDAO.save(bill);
        return bill;
    }

    /** Full patient/dentist/treatment details for the printed receipt. */
    public Map<String, Object> getReceiptDetails(String appointmentNumber) {
        return billDAO.getReceiptDetails(appointmentNumber);
    }
}
