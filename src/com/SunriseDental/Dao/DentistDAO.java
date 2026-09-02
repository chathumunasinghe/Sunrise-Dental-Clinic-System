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

    public List<Dentist> getAllDentists() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                dentists.add(new Dentist(rs.getInt("dentist_id"), rs.getString("name"),
                        rs.getString("specialization")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dentists;
    }

    public Dentist findById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Dentist(rs.getInt("dentist_id"), rs.getString("name"), rs.getString("specialization"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
