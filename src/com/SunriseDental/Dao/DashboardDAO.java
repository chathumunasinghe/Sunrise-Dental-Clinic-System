package com.SunriseDental.Dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Read-only dashboard queries used to give staff a useful clinic overview. */
public class DashboardDAO {
    private final Connection conn;

    public DashboardDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    private int count(String sql, Object... params) {
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTodayAppointments() {
        return count("SELECT COUNT(*) FROM appointments WHERE appointment_date = ?", Date.valueOf(LocalDate.now()));
    }

    public int getTotalPatients() {
        return count("SELECT COUNT(*) FROM patients");
    }

    public int getActiveStaff() {
        return count("SELECT COUNT(*) FROM staff WHERE status = 'ACTIVE'");
    }

    public double getTodayRevenue() {
        if (conn == null) return 0.0;
        String sql = "SELECT COALESCE(SUM(b.total_amount),0) FROM bills b " +
                     "JOIN appointments a ON a.appointment_number=b.appointment_number " +
                     "WHERE a.appointment_date=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public List<Map<String, Object>> getTodaySchedule(int limit) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (conn == null) return rows;
        String sql = "SELECT a.appointment_number, a.appointment_time, a.status, " +
                     "p.name patient_name, d.name dentist_name, t.treatment_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON p.patient_id=a.patient_id " +
                     "JOIN dentists d ON d.dentist_id=a.dentist_id " +
                     "JOIN treatment_types t ON t.treatment_id=a.treatment_id " +
                     "WHERE a.appointment_date=? ORDER BY a.appointment_time ASC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("appointment_number", rs.getString("appointment_number"));
                    row.put("appointment_time", rs.getString("appointment_time"));
                    row.put("status", rs.getString("status"));
                    row.put("patient_name", rs.getString("patient_name"));
                    row.put("dentist_name", rs.getString("dentist_name"));
                    row.put("treatment_name", rs.getString("treatment_name"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
