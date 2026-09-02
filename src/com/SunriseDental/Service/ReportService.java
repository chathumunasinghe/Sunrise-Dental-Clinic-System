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

    public Map<String, Double> getRevenueByTreatmentReport() {
        return reportDAO.getRevenueByTreatmentType();
    }

    public double getTotalRevenue() {
        return reportDAO.getTotalRevenue();
    }
}
