package com.SunriseDental.Dao;

import com.SunriseDental.Model.Bill;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BillDAO {
    private Connection conn;
    private static boolean paymentMethodColumnChecked = false;

    public BillDAO() {
        conn = DBConnection.getInstance().getConnection();
        ensurePaymentMethodColumn();
    }

    /**
     * Self-heals older databases created before cash-payment tracking was
     * added, so admins upgrading an existing install don't have to manually
     * re-run schema.sql. Runs once per app lifetime.
     */
    private void ensurePaymentMethodColumn() {
        if (paymentMethodColumnChecked || conn == null) return;
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("ALTER TABLE bills ADD COLUMN IF NOT EXISTS payment_method ENUM('ONLINE','CASH') NULL");
            paymentMethodColumnChecked = true;
        } catch (SQLException e) {
            // Older MariaDB/MySQL versions without "IF NOT EXISTS" support will
            // throw a duplicate-column error on repeat startups — harmless.
            paymentMethodColumnChecked = true;
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill(rs.getString("appointment_number"), rs.getDouble("total_amount"));
        bill.setBillId(rs.getInt("bill_id"));
        bill.setPaid(rs.getBoolean("paid"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        if (rs.getTimestamp("paid_at") != null) {
            bill.setPaidAt(rs.getTimestamp("paid_at").toString());
        }
        if (rs.getTimestamp("issue_date") != null) {
            bill.setIssueDate(rs.getTimestamp("issue_date").toString());
        }
        return bill;
    }

    /** Returns the existing bill for an appointment, or null if none has been generated yet. */
    public Bill findByAppointmentNumber(String appointmentNumber) {
        String sql = "SELECT * FROM bills WHERE appointment_number = ? ORDER BY bill_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Full receipt details for printing: patient contact info, dentist,
     * treatment, appointment date/time, and the bill itself, all in one row.
     * Returns null if no bill has been generated for this appointment yet.
     */
    public Map<String, Object> getReceiptDetails(String appointmentNumber) {
        String sql = "SELECT b.bill_id, b.total_amount, b.issue_date, b.paid, b.paid_at, b.payment_method, " +
                     "a.appointment_number, a.appointment_date, a.appointment_time, a.status, " +
                     "p.name AS patient_name, p.contact_number AS patient_contact, " +
                     "p.email AS patient_email, p.address AS patient_address, " +
                     "d.name AS dentist_name, d.specialization, " +
                     "t.treatment_name " +
                     "FROM bills b " +
                     "JOIN appointments a ON a.appointment_number = b.appointment_number " +
                     "JOIN patients p ON p.patient_id = a.patient_id " +
                     "JOIN dentists d ON d.dentist_id = a.dentist_id " +
                     "JOIN treatment_types t ON t.treatment_id = a.treatment_id " +
                     "WHERE b.appointment_number = ? " +
                     "ORDER BY b.bill_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    return row;
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

    /** Marks a bill paid via the patient's own online checkout — sets paid_at to now. */
    public boolean markPaid(String appointmentNumber) {
        return markPaid(appointmentNumber, "ONLINE");
    }

    /**
     * Marks a bill as paid, recording how it was paid — "ONLINE" for the
     * patient's own checkout, or "CASH" when front-desk/admin staff record a
     * walk-in cash payment on the billing screen.
     */
    public boolean markPaid(String appointmentNumber, String method) {
        String sql = "UPDATE bills SET paid = 1, paid_at = NOW(), payment_method = ? WHERE appointment_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, method);
            ps.setString(2, appointmentNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
