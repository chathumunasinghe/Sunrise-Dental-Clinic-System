package com.SunriseDental.Dao;

import com.SunriseDental.Model.TreatmentType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentTypeDAO {
    private Connection conn;

    public TreatmentTypeDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public List<TreatmentType> getAllTreatmentTypes() {
        List<TreatmentType> types = new ArrayList<>();
        String sql = "SELECT * FROM treatment_types";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                types.add(new TreatmentType(rs.getInt("treatment_id"), rs.getString("treatment_name"),
                        rs.getDouble("consultation_fee")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public double getFee(int treatmentId) {
        String sql = "SELECT consultation_fee FROM treatment_types WHERE treatment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, treatmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("consultation_fee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
