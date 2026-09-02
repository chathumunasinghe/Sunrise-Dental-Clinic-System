package com.SunriseDental.Dao;

import com.SunriseDental.Model.Bill;
import java.sql.*;

public class BillDAO {
    private Connection conn;

    public BillDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    /** Returns the existing bill for an appointment, or null if none has been generated yet. */
    public Bill findByAppointmentNumber(String appointmentNumber) {
        String sql = "SELECT * FROM bills WHERE appointment_number = ? ORDER BY bill_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill(rs.getString("appointment_number"), rs.getDouble("total_amount"));
                    bill.setBillId(rs.getInt("bill_id"));
                    if (rs.getTimestamp("issue_date") != null) {
                        bill.setIssueDate(rs.getTimestamp("issue_date").toString());
                    }
                    return bill;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Bill bill) {
        String sql = "INSERT INTO bills (appointment_number, total_amount) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bill.getAppointmentNumber());
            ps.setDouble(2, bill.getTotalAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
