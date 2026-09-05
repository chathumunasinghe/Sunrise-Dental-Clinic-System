package com.SunriseDental.Model;

public class Dentist {
    private int dentistId;
    private String name;
    private String specialization;
    // Extra profile details shown on the patient-facing doctor detail page.
    private String qualification;
    private Integer experienceYears;
    private String bio;
    private String email;
    private String consultationDays;
    private String status; // "ACTIVE" or "DISABLED"

    public Dentist() {}

    public Dentist(int dentistId, String name, String specialization) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
    }

    public Dentist(int dentistId, String name, String specialization, String qualification,
                   Integer experienceYears, String bio, String email, String consultationDays, String status) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.bio = bio;
        this.email = email;
        this.consultationDays = consultationDays;
        this.status = status;
    }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getConsultationDays() { return consultationDays; }
    public void setConsultationDays(String consultationDays) { this.consultationDays = consultationDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return status == null || "ACTIVE".equalsIgnoreCase(status); }
}
