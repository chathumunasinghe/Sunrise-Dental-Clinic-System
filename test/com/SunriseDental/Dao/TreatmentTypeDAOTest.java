package com.SunriseDental.Dao;

import com.SunriseDental.Model.TreatmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TreatmentTypeDAOTest {

    private TreatmentTypeDAO treatmentTypeDAO;

    @BeforeEach
    void setUp() {
        treatmentTypeDAO = new TreatmentTypeDAO();
    }

    @Test
    void testGetAllTreatmentTypes_ReturnsSeededData() {
        List<TreatmentType> types = treatmentTypeDAO.getAllTreatmentTypes();
        assertNotNull(types);
        assertTrue(types.size() >= 4, "Schema seeds 4 treatment types");
    }

    @Test
    void testGetFee_KnownTreatment() {
        // Treatment ID 1 = Consultation, seeded at 1500.00 in schema.sql
        double fee = treatmentTypeDAO.getFee(1);
        assertEquals(1500.00, fee, 0.01);
    }

    @Test
    void testGetFee_UnknownTreatment_ReturnsZero() {
        double fee = treatmentTypeDAO.getFee(999);
        assertEquals(0.0, fee, 0.01);
    }
}
