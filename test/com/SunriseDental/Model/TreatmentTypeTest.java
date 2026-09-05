package com.SunriseDental.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TreatmentTypeTest {

    @Test
    void testConstructorAndGetters() {
        TreatmentType t = new TreatmentType(1, "Root Canal", 15000.00);
        assertEquals(1, t.getTreatmentId());
        assertEquals("Root Canal", t.getTreatmentName());
        assertEquals(15000.00, t.getConsultationFee());
    }

    @Test
    void testSettersAndGetters() {
        TreatmentType t = new TreatmentType();
        t.setTreatmentId(2);
        t.setTreatmentName("Tooth Filling");
        t.setConsultationFee(4500.00);

        assertEquals(2, t.getTreatmentId());
        assertEquals("Tooth Filling", t.getTreatmentName());
        assertEquals(4500.00, t.getConsultationFee());
    }
}
