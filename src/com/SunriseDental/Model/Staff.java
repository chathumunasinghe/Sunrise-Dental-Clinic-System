package com.SunriseDental.Model;

public class Staff {
    private int staffId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;   // "ADMIN" or "GUEST"
    private String status; // "ACTIVE" or "DISABLED"

    public Staff() {}

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
}
