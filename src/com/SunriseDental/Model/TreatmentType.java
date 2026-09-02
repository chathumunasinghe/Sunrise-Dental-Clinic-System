package com.SunriseDental.Model;

public class TreatmentType {
    private int treatmentId;
    private String treatmentName;
    private double consultationFee;

    public TreatmentType() {}

    public TreatmentType(int treatmentId, String treatmentName, double consultationFee) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.consultationFee = consultationFee;
    }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
}
