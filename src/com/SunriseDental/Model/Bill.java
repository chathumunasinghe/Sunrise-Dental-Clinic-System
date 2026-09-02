package com.SunriseDental.Model;

public class Bill {
    private int billId;
    private String appointmentNumber;
    private double totalAmount;
    private String issueDate;

    public Bill() {}

    public Bill(String appointmentNumber, double totalAmount) {
        this.appointmentNumber = appointmentNumber;
        this.totalAmount = totalAmount;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
}
