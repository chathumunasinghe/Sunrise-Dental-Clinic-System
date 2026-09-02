package com.SunriseDental.Dao;

import com.SunriseDental.Model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatientDAO {
    private Connection conn;

    public PatientDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean save(Patient patient) {
        String sql = "INSERT INTO patients (patient_id, name, address, contact_number, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getPatientId());
            ps.setString(2, patient.getName());
            ps.setString(3, patient.getAddress());
            ps.setString(4, patient.getContactNumber());
            ps.setString(5, patient.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Patient findById(String patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Patient(rs.getString("patient_id"), rs.getString("name"),
                        rs.getString("address"), rs.getString("contact_number"), rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Patient directory for the admin "Patients" screen: every registered
     * patient together with how many visits they've had and their most
     * recent visit date (both derived from the appointments table).
     */
    public List<Map<String, Object>> getPatientDirectory() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.patient_id, p.name, p.contact_number, p.email, p.address, " +
                "COUNT(a.appointment_number) AS visit_count, MAX(a.appointment_date) AS last_visit " +
                "FROM patients p LEFT JOIN appointments a ON a.patient_id = p.patient_id " +
                "GROUP BY p.patient_id, p.name, p.contact_number, p.email, p.address " +
                "ORDER BY p.patient_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("patient_id", rs.getString("patient_id"));
                row.put("name", rs.getString("name"));
                row.put("contact_number", rs.getString("contact_number"));
                row.put("email", rs.getString("email"));
                row.put("address", rs.getString("address"));
                row.put("visit_count", rs.getInt("visit_count"));
                row.put("last_visit", rs.getDate("last_visit"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Generates the next patient ID, e.g. PT0001, PT0002 ... */
    public String getNextPatientId() {
        String sql = "SELECT MAX(patient_id) AS maxId FROM patients";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString("maxId") != null) {
                int num = Integer.parseInt(rs.getString("maxId").substring(2)) + 1;
                return String.format("PT%04d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PT0001";
    }
}
