package com.SunriseDental.Dao;

import com.SunriseDental.Model.Staff;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private Connection conn;
    private static final SecureRandom RANDOM = new SecureRandom();
    private boolean resetTableChecked = false;

    public StaffDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setUsername(rs.getString("username"));
        staff.setFullName(rs.getString("full_name"));
        staff.setEmail(rs.getString("email"));
        staff.setRole(rs.getString("role"));
        staff.setStatus(rs.getString("status"));
        int dentistId = rs.getInt("dentist_id");
        staff.setDentistId(rs.wasNull() ? null : dentistId);
        return staff;
    }

    /** Validates login credentials against the staff table (active accounts only). */
    public Staff validateLogin(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ? AND status = 'ACTIVE'";
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

    /** Looks a staff member up by username OR email — used by the "Forgot password" flow. */
    public Staff findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM staff WHERE (username = ? OR email = ?) AND status = 'ACTIVE'";
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

    /** Ensures older project databases also support the password-reset flow. */
    private void ensurePasswordResetTable() {
        if (resetTableChecked || conn == null) return;
        String sql = "CREATE TABLE IF NOT EXISTS password_reset_tokens (" +
                "token_id INT AUTO_INCREMENT PRIMARY KEY," +
                "staff_id INT NOT NULL," +
                "token VARCHAR(64) UNIQUE NOT NULL," +
                "expires_at TIMESTAMP NOT NULL," +
                "used TINYINT(1) NOT NULL DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE)";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
            resetTableChecked = true;
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Generates a one-time reset token valid for 30 minutes and stores it. */
    public String createPasswordResetToken(int staffId) {
        ensurePasswordResetTable();
        String token = generateToken();
        String sql = "INSERT INTO password_reset_tokens (staff_id, token, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)));
            ps.executeUpdate();
            return token;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns the staff_id for a valid, unused, unexpired token — or -1 if invalid. */
    public int validateResetToken(String token) {
        ensurePasswordResetTable();
        if (token == null || token.isBlank()) return -1;
        String sql = "SELECT staff_id FROM password_reset_tokens " +
                     "WHERE token = ? AND used = 0 AND expires_at > NOW()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("staff_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Updates the password for a staff member and marks the token as used. */
    public boolean resetPassword(String token, int staffId, String newPassword) {
        ensurePasswordResetTable();
        String updatePwd = "UPDATE staff SET password = ? WHERE staff_id = ?";
        String markUsed = "UPDATE password_reset_tokens SET used = 1 WHERE token = ?";
        try (PreparedStatement ps1 = conn.prepareStatement(updatePwd);
             PreparedStatement ps2 = conn.prepareStatement(markUsed)) {
            ps1.setString(1, newPassword);
            ps1.setInt(2, staffId);
            ps1.executeUpdate();

            ps2.setString(1, token);
            ps2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Lists all staff accounts — used on the admin-only "Manage Staff" screen. */
    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY staff_id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Creates a new staff account (admin-only function). Role must be ADMIN or GUEST. */
    public boolean addStaff(String username, String password, String fullName, String email, String role) {
        String sql = "INSERT INTO staff (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Enables/disables a staff account (admin-only function). Disabled accounts can't log in. */
    public boolean setStaffStatus(int staffId, String status) {
        String sql = "UPDATE staff SET status = ? WHERE staff_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, staffId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permanently removes a staff account (admin-only function). Any of that
     * staff member's password-reset tokens are removed automatically via the
     * table's ON DELETE CASCADE foreign key.
     */
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            return ps.executeUpdate() > 0;
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
