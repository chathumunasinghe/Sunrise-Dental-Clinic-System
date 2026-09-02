CREATE DATABASE IF NOT EXISTS sunrise_dental;
USE sunrise_dental;

CREATE TABLE staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    role ENUM('ADMIN', 'GUEST') NOT NULL DEFAULT 'GUEST',
    status ENUM('ACTIVE', 'DISABLED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores one-time tokens for the "Forgot password" flow. Tokens expire
-- and are marked used so a link cannot be replayed after a reset.
CREATE TABLE password_reset_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT NOT NULL,
    token VARCHAR(64) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE CASCADE
);

CREATE TABLE patients (
    patient_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE dentists (
    dentist_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100)
);

CREATE TABLE treatment_types (
    treatment_id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name VARCHAR(100) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL
);

CREATE TABLE appointments (
    appointment_number VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    dentist_id INT NOT NULL,
    treatment_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'Scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
    FOREIGN KEY (treatment_id) REFERENCES treatment_types(treatment_id)
);

CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(10) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_number) REFERENCES appointments(appointment_number)
);

-- View for reporting (Excellent-band "database views" feature)
CREATE VIEW appointment_summary AS
SELECT a.appointment_number, p.name AS patient_name, d.name AS dentist_name,
       t.treatment_name, t.consultation_fee, a.appointment_date, a.status
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN dentists d ON a.dentist_id = d.dentist_id
JOIN treatment_types t ON a.treatment_id = t.treatment_id;

-- Sample reference data
-- Admin: full access (reports, staff management, billing, appointments)
-- Guest: front-desk access only (appointments, search, billing) — no reports/staff management
INSERT INTO staff (username, password, full_name, email, role) VALUES
    ('admin', 'admin123', 'Nadeesha Fernando', 'admin@sunrisedental.lk', 'ADMIN'),
    ('guest', 'guest123', 'Kasun Jayasuriya', 'guest@sunrisedental.lk', 'GUEST');

INSERT INTO dentists (name, specialization) VALUES
    ('Dr. Perera', 'General Dentistry'),
    ('Dr. Silva', 'Orthodontics');
INSERT INTO treatment_types (treatment_name, consultation_fee) VALUES
    ('Consultation', 1500.00),
    ('Tooth Filling', 4500.00),
    ('Tooth Extraction', 6000.00),
    ('Root Canal', 15000.00);
