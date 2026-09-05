package com.SunriseDental.Service;

import com.SunriseDental.Dao.ReportDAO;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    public List<Map<String, Object>> getDailyAppointmentReport(Date date) {
        return reportDAO.getDailyAppointments(date);
    }

    /** Revenue actually collected (paid bills only), split CASH vs ONLINE. */
    public Map<String, Double> getRevenueByPaymentMethod() {
        return reportDAO.getRevenueByPaymentMethod();
    }

    /** How much is still owed across all unpaid bills, and how many. */
    public Map<String, Object> getPendingPayments() {
        return reportDAO.getPendingPayments();
    }

    /** Who still owes money — one row per unpaid bill, with contact details. */
    public List<Map<String, Object>> getPendingPaymentDetails() {
        return reportDAO.getPendingPaymentDetails();
    }

    /** Appointment load per dentist for a given date. */
    public List<Map<String, Object>> getAppointmentsByDentist(Date date) {
        return reportDAO.getAppointmentsByDentist(date);
    }

    /** Every bill (paid + unpaid), optionally filtered by a search term. */
    public List<Map<String, Object>> getAllBills(String search) {
        return reportDAO.getAllBills(search);
    }
}
