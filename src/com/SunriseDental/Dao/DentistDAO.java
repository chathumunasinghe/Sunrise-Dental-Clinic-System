package com.SunriseDental.Dao;

import com.SunriseDental.Model.Dentist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {
    private Connection conn;

    public DentistDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        int experience = rs.getInt("experience_years");
        return new Dentist(
                rs.getInt("dentist_id"),
                rs.getString("name"),
                rs.getString("specialization"),
                rs.getString("qualification"),
                rs.wasNull() ? null : experience,
                rs.getString("bio"),
                rs.getString("email"),
                rs.getString("consultation_days"),
                rs.getString("status"));
    }

    public List<Dentist> getAllDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                dentists.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dentists;
    }

    /**
     * Doctors patients are currently allowed to book with — excludes any
     * dentist an admin has marked DISABLED (e.g. on leave / no longer at
     * the clinic). Used by the patient-facing Doctors list and the
     * front-desk "New Appointment" dentist dropdown.
     */
    public List<Dentist> getActiveDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE status = 'ACTIVE'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                dentists.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dentists;
    }

    /** Admin-only: enable/disable a doctor so patients can no longer book new appointments with them. */
    public boolean setStatus(int dentistId, String status) {
        String sql = "UPDATE dentists SET status = ? WHERE dentist_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, dentistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Dentist findById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
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
     * Admin-only: creates a brand-new doctor — a profile row in
     * {@code dentists} plus a linked staff login (role=DENTIST) — in one
     * transaction, so a failure partway through (e.g. a duplicate username)
     * never leaves a profile-only or login-only doctor behind.
     * Returns the new dentist_id, or -1 if creation failed.
     */
    public int createDoctorAccount(String name, String specialization, String qualification,
                                    Integer experienceYears, String bio, String email, String consultationDays,
                                    String username, String password) {
        String insertDentist = "INSERT INTO dentists (name, specialization, qualification, experience_years, bio, email, consultation_days) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertStaff = "INSERT INTO staff (username, password, full_name, email, role, dentist_id) " +
                "VALUES (?, ?, ?, ?, 'DENTIST', ?)";

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            int dentistId;
            try (PreparedStatement ps = conn.prepareStatement(insertDentist, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, specialization);
                ps.setString(3, qualification);
                if (experienceYears == null) {
                    ps.setNull(4, Types.INTEGER);
                } else {
                    ps.setInt(4, experienceYears);
                }
                ps.setString(5, bio);
                ps.setString(6, email);
                ps.setString(7, consultationDays);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Doctor profile insert did not return a generated id.");
                    }
                    dentistId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertStaff)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, name);
                ps.setString(4, email);
                ps.setInt(5, dentistId);
                ps.executeUpdate();
            }

            conn.commit();
            return dentistId;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return -1;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
