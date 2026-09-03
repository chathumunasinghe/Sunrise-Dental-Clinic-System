package com.SunriseDental.Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-app notifications shown on the patient dashboard — created whenever
 * staff (or the patient themself) create an appointment, or when its
 * status changes, so the patient sees an update without needing email/SMS.
 */
public class NotificationDAO {
    private Connection conn;

    public NotificationDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean create(String patientId, String appointmentNumber, String message) {
        String sql = "INSERT INTO notifications (patient_id, appointment_number, message) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, appointmentNumber);
            ps.setString(3, message);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Most recent notifications for one patient, newest first. */
    public List<Map<String, Object>> getForPatient(String patientId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT notification_id, appointment_number, message, is_read, created_at " +
                "FROM notifications WHERE patient_id = ? ORDER BY created_at DESC LIMIT 20";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("notification_id", rs.getInt("notification_id"));
                    row.put("appointment_number", rs.getString("appointment_number"));
                    row.put("message", rs.getString("message"));
                    row.put("is_read", rs.getBoolean("is_read"));
                    row.put("created_at", rs.getTimestamp("created_at"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnreadCount(String patientId) {
        String sql = "SELECT COUNT(*) AS cnt FROM notifications WHERE patient_id = ? AND is_read = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Marks every notification for this patient as read — called when they open the notifications list. */
    public boolean markAllRead(String patientId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE patient_id = ? AND is_read = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
