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

    /**
     * Total amount actually collected (paid bills only — unlike a raw sum
     * over every bill, this doesn't count money the clinic hasn't received
     * yet), split by how it was paid: CASH at the front desk vs the
     * patient's own ONLINE checkout. Practical for reconciling the day's
     * cash drawer against what came in through the portal.
     */
    public Map<String, Double> getRevenueByPaymentMethod() {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT COALESCE(payment_method, 'ONLINE') AS method, SUM(total_amount) AS total " +
                     "FROM bills WHERE paid = 1 GROUP BY COALESCE(payment_method, 'ONLINE')";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("method"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * How many bills are still unpaid, and how much they total — what the
     * clinic is still owed. Keys: "count" (Long) and "total" (Double).
     */
    public Map<String, Object> getPendingPayments() {
        Map<String, Object> result = new LinkedHashMap<>();
        String sql = "SELECT COUNT(*) AS cnt, COALESCE(SUM(total_amount), 0) AS total FROM bills WHERE paid = 0";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                result.put("count", rs.getLong("cnt"));
                result.put("total", rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Every unpaid bill with enough detail to actually chase it down —
     * patient name/contact, treatment, appointment date, and amount owed.
     * The plain count/total from {@link #getPendingPayments()} tells admin
     * *how much* is outstanding; this tells them *who* still needs to pay.
     */
    public List<Map<String, Object>> getPendingPaymentDetails() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT b.appointment_number, b.total_amount, b.issue_date, " +
                     "p.name AS patient_name, p.contact_number, " +
                     "a.appointment_date, t.treatment_name " +
                     "FROM bills b " +
                     "JOIN appointments a ON a.appointment_number = b.appointment_number " +
                     "JOIN patients p ON p.patient_id = a.patient_id " +
                     "JOIN treatment_types t ON t.treatment_id = a.treatment_id " +
                     "WHERE b.paid = 0 " +
                     "ORDER BY a.appointment_date ASC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("appointment_number", rs.getString("appointment_number"));
                row.put("patient_name", rs.getString("patient_name"));
                row.put("contact_number", rs.getString("contact_number"));
                row.put("treatment_name", rs.getString("treatment_name"));
                row.put("appointment_date", rs.getDate("appointment_date"));
                row.put("total_amount", rs.getDouble("total_amount"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * How many appointments each dentist has on a given date (excluding
     * cancelled ones) — a quick way to see whose day is packed and who has
     * room, instead of a per-treatment revenue split that doesn't help
     * with staffing decisions.
     */
    public List<Map<String, Object>> getAppointmentsByDentist(Date date) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT d.name AS dentist_name, COUNT(*) AS appt_count " +
                     "FROM appointments a JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "WHERE a.appointment_date = ? AND a.status <> 'Cancelled' " +
                     "GROUP BY d.name ORDER BY appt_count DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("dentist_name", rs.getString("dentist_name"));
                    row.put("appt_count", rs.getInt("appt_count"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Every bill — paid and unpaid — with patient/treatment detail and an
     * optional search filter (matches patient name, contact number, or
     * appointment number). Powers the front-desk "Payments" list so
     * reception can see at a glance who has paid without needing to
     * already know the exact appointment number, and without needing
     * admin-only Reports access.
     */
    public List<Map<String, Object>> getAllBills(String search) {
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT b.appointment_number, b.total_amount, b.issue_date, b.paid, b.payment_method, " +
                "p.name AS patient_name, p.contact_number, " +
                "a.appointment_date, t.treatment_name " +
                "FROM bills b " +
                "JOIN appointments a ON a.appointment_number = b.appointment_number " +
                "JOIN patients p ON p.patient_id = a.patient_id " +
                "JOIN treatment_types t ON t.treatment_id = a.treatment_id ");

        boolean hasSearch = search != null && !search.trim().isEmpty();
        if (hasSearch) {
            sql.append("WHERE p.name LIKE ? OR p.contact_number LIKE ? OR b.appointment_number LIKE ? ");
        }
        sql.append("ORDER BY b.paid ASC, a.appointment_date DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasSearch) {
                String like = "%" + search.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("appointment_number", rs.getString("appointment_number"));
                    row.put("patient_name", rs.getString("patient_name"));
                    row.put("contact_number", rs.getString("contact_number"));
                    row.put("treatment_name", rs.getString("treatment_name"));
                    row.put("appointment_date", rs.getDate("appointment_date"));
                    row.put("total_amount", rs.getDouble("total_amount"));
                    row.put("paid", rs.getBoolean("paid"));
                    row.put("payment_method", rs.getString("payment_method"));
                    row.put("issue_date", rs.getTimestamp("issue_date"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
