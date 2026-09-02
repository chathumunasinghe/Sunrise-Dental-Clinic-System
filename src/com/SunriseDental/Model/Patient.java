package com.SunriseDental.Model;

public class Patient {
    private String patientId;
    private String name;
    private String address;
    private String contactNumber;
    private String email;

    public Patient() {}

    public Patient(String patientId, String name, String address, String contactNumber) {
        this.patientId = patientId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public Patient(String patientId, String name, String address, String contactNumber, String email) {
        this(patientId, name, address, contactNumber);
        this.email = email;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
