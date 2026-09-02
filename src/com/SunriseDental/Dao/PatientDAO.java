package com.SunriseDental.Dao;

import com.SunriseDental.Model.Patient;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatientDAO {
    private Connection conn;
    private static final SecureRandom RANDOM = new SecureRandom();

    public PatientDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient(rs.getString("patient_id"), rs.getString("name"),
                rs.getString("address"), rs.getString("contact_number"), rs.getString("email"));
        p.setUsername(rs.getString("username"));
        return p;
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
                return mapRow(rs);
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

    // ---------------------------------------------------------------
    // Patient portal account: sign up / sign in / forgot password.
    // Mirrors the pattern used by StaffDAO for the staff side.
    // ---------------------------------------------------------------

    /** True if the given username is already taken by another patient account. */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM patients WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a brand-new patient account for the online "Create Account"
     * form (as opposed to the front-desk save() above, which registers a
     * patient without portal login credentials).
     */
    public String registerPatient(String name, String address, String contactNumber,
                                   String email, String username, String password) {
        String patientId = getNextPatientId();
        String sql = "INSERT INTO patients (patient_id, name, address, contact_number, email, username, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, name);
            ps.setString(3, address);
            ps.setString(4, contactNumber);
            ps.setString(5, email);
            ps.setString(6, username);
            ps.setString(7, password);
            return ps.executeUpdate() > 0 ? patientId : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Validates a patient portal login. */
    public Patient validateLogin(String username, String password) {
        String sql = "SELECT * FROM patients WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Looks a patient up by portal username OR email — used by "Forgot password". */
    public Patient findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM patients WHERE username = ? OR email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Generates a one-time reset token valid for 30 minutes and stores it. */
    public String createPasswordResetToken(String patientId) {
        String token = generateToken();
        String sql = "INSERT INTO patient_reset_tokens (patient_id, token, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)));
            ps.executeUpdate();
            return token;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns the patient_id for a valid, unused, unexpired token — or null if invalid. */
    public String validateResetToken(String token) {
        if (token == null || token.isBlank()) return null;
        String sql = "SELECT patient_id FROM patient_reset_tokens WHERE token = ? AND used = 0 AND expires_at > NOW()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("patient_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Updates the portal password for a patient and marks the token as used. */
    public boolean resetPassword(String token, String patientId, String newPassword) {
        String updatePwd = "UPDATE patients SET password = ? WHERE patient_id = ?";
        String markUsed = "UPDATE patient_reset_tokens SET used = 1 WHERE token = ?";
        try (PreparedStatement ps1 = conn.prepareStatement(updatePwd);
             PreparedStatement ps2 = conn.prepareStatement(markUsed)) {
            ps1.setString(1, newPassword);
            ps1.setString(2, patientId);
            ps1.executeUpdate();

            ps2.setString(1, token);
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
