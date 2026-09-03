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
}
