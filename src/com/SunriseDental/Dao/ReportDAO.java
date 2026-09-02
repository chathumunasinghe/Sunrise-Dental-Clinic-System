package com.SunriseDental.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {
    private Connection conn;

    public ReportDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    /** Uses the appointment_summary database view for a simple daily list of appointments. */
    public List<Map<String, Object>> getDailyAppointments(Date date) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT * FROM appointment_summary WHERE appointment_date = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /** Total revenue grouped by treatment type, joining bills to appointments. */
    public Map<String, Double> getRevenueByTreatmentType() {
        Map<String, Double> revenue = new LinkedHashMap<>();
        String sql = "SELECT t.treatment_name, SUM(b.total_amount) AS total " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appointment_number = a.appointment_number " +
                     "JOIN treatment_types t ON a.treatment_id = t.treatment_id " +
                     "GROUP BY t.treatment_name";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                revenue.put(rs.getString("treatment_name"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) AS total FROM bills";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
