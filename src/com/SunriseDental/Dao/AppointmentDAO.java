package com.SunriseDental.Dao;

import com.SunriseDental.Model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDAO {
    private Connection conn;

    public AppointmentDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    /** Generates a unique appointment number, e.g. APT0001, APT0002 ... */
    public String getNextAppointmentNumber() {
        String sql = "SELECT MAX(appointment_number) AS maxNum FROM appointments";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("maxNum") != null) {
                int num = Integer.parseInt(rs.getString("maxNum").substring(3)) + 1;
                return String.format("APT%04d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "APT0001";
    }

    public boolean save(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, "
                + "appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointment.getAppointmentNumber());
            ps.setString(2, appointment.getPatientId());
            ps.setInt(3, appointment.getDentistId());
            ps.setInt(4, appointment.getTreatmentId());
            ps.setDate(5, appointment.getAppointmentDate());
            ps.setString(6, appointment.getAppointmentTime());
            ps.setString(7, appointment.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Full patient + doctor + treatment history for the admin "Patients"
     * screen — every appointment ever booked, joined against patients,
     * dentists and treatment types so each row is a complete record.
     */
    public List<Map<String, Object>> getFullHistory() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.appointment_number, a.patient_id, p.name AS patient_name, " +
                "d.name AS dentist_name, d.specialization, " +
                "t.treatment_name, t.consultation_fee, " +
                "a.appointment_date, a.appointment_time, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "JOIN treatment_types t ON a.treatment_id = t.treatment_id " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("appointment_number", rs.getString("appointment_number"));
                row.put("patient_id", rs.getString("patient_id"));
                row.put("patient_name", rs.getString("patient_name"));
                row.put("dentist_name", rs.getString("dentist_name"));
                row.put("specialization", rs.getString("specialization"));
                row.put("treatment_name", rs.getString("treatment_name"));
                row.put("consultation_fee", rs.getDouble("consultation_fee"));
                row.put("appointment_date", rs.getDate("appointment_date"));
                row.put("appointment_time", rs.getString("appointment_time"));
                row.put("status", rs.getString("status"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Every appointment booked by one patient, most recent first, with the
     * dentist/treatment names and whether a bill has been paid — used by
     * the patient portal's "My Appointments" list.
     */
    public List<Map<String, Object>> getByPatientId(String patientId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.appointment_number, d.name AS dentist_name, d.specialization, " +
                "t.treatment_name, t.consultation_fee, a.appointment_date, a.appointment_time, a.status, " +
                "b.paid AS bill_paid, b.bill_id " +
                "FROM appointments a " +
                "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "JOIN treatment_types t ON a.treatment_id = t.treatment_id " +
                "LEFT JOIN bills b ON b.appointment_number = a.appointment_number " +
                "WHERE a.patient_id = ? " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("appointment_number", rs.getString("appointment_number"));
                    row.put("dentist_name", rs.getString("dentist_name"));
                    row.put("specialization", rs.getString("specialization"));
                    row.put("treatment_name", rs.getString("treatment_name"));
                    row.put("consultation_fee", rs.getDouble("consultation_fee"));
                    row.put("appointment_date", rs.getDate("appointment_date"));
                    row.put("appointment_time", rs.getString("appointment_time"));
                    row.put("status", rs.getString("status"));
                    row.put("has_bill", rs.getObject("bill_id") != null);
                    row.put("paid", rs.getBoolean("bill_paid"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates an appointment's status — used by the front desk (guest or
     * admin) to mark a visit as Completed once the patient has met the
     * doctor, or as Cancelled.
     */
    public boolean updateStatus(String appointmentNumber, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, appointmentNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Appointment findByNumber(String appointmentNumber) {
        String sql = "SELECT * FROM appointments WHERE appointment_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentNumber(rs.getString("appointment_number"));
                a.setPatientId(rs.getString("patient_id"));
                a.setDentistId(rs.getInt("dentist_id"));
                a.setTreatmentId(rs.getInt("treatment_id"));
                a.setAppointmentDate(rs.getDate("appointment_date"));
                a.setAppointmentTime(rs.getString("appointment_time"));
                a.setStatus(rs.getString("status"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
