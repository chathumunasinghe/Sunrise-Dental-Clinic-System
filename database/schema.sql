-- Always start from a clean slate. This prevents "Table already exists"
-- errors (and the silent login failures that follow from a stale 'staff'
-- table missing newer columns) when re-running this script.
DROP DATABASE IF EXISTS sunrise_dental;
CREATE DATABASE sunrise_dental;
USE sunrise_dental;

CREATE TABLE staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    role ENUM('ADMIN', 'GUEST', 'DENTIST') NOT NULL DEFAULT 'GUEST',
    -- Only set for role='DENTIST': links this login to the dentist's own
    -- profile row in the dentists table, so their dashboard can be scoped
    -- to just their own assigned appointments. No FK constraint here since
    -- the dentists table is created later in this script — kept as a
    -- documented design assumption rather than reordering every table.
    dentist_id INT NULL,
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
    email VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255)
);

-- One-time tokens for the patient-facing "Forgot password" flow, mirroring
-- password_reset_tokens but scoped to the patients table.
CREATE TABLE patient_reset_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    token VARCHAR(64) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
);

CREATE TABLE dentists (
    dentist_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    -- Extra profile fields for the patient-facing doctor detail page.
    qualification VARCHAR(150),
    experience_years INT,
    bio VARCHAR(500),
    email VARCHAR(100),
    consultation_days VARCHAR(150),
    -- Lets admin temporarily take a doctor off the patient-facing booking
    -- flow (e.g. on leave) without deleting their record or history.
    status ENUM('ACTIVE', 'DISABLED') NOT NULL DEFAULT 'ACTIVE'
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
    -- Filled in by the assigned dentist after seeing the patient.
    treatment_notes TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
    FOREIGN KEY (treatment_id) REFERENCES treatment_types(treatment_id)
);

CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(10) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    paid TINYINT(1) NOT NULL DEFAULT 0,
    payment_method ENUM('ONLINE', 'CASH') NULL,
    paid_at TIMESTAMP NULL,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_number) REFERENCES appointments(appointment_number)
);

-- In-app notifications shown on the patient dashboard. Created whenever
-- staff register a new appointment (or update its status), so the patient
-- sees it the next time they log in without needing email/SMS.
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    appointment_number VARCHAR(10) NULL,
    message VARCHAR(255) NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_number) REFERENCES appointments(appointment_number) ON DELETE SET NULL
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
-- Admin: full access (reports, staff management, treatment prices, appointments)
-- Guest (Receptionist): register patients, create/search appointments, generate bills
-- Dentist: view only their own assigned appointments and add treatment notes
INSERT INTO staff (username, password, full_name, email, role, dentist_id) VALUES
    ('admin', 'admin123', 'Nadeesha Fernando', 'admin@sunrisedental.lk', 'ADMIN', NULL),
    ('guest', 'guest123', 'Kasun Jayasuriya', 'guest@sunrisedental.lk', 'GUEST', NULL),
    ('dr.perera', 'dentist123', 'Dr. Perera', 'perera@sunrisedental.lk', 'DENTIST', 1),
    ('dr.silva', 'dentist123', 'Dr. Silva', 'silva@sunrisedental.lk', 'DENTIST', 2);

INSERT INTO dentists (name, specialization, qualification, experience_years, bio, email, consultation_days) VALUES
    ('Dr. Perera', 'General Dentistry', 'BDS (Colombo), Dip. in Clinical Dentistry',
     12, 'Dr. Perera has spent over a decade helping patients of all ages with routine checkups, fillings, and preventive care. Known for a calm chairside manner, he takes extra time to explain every step to nervous first-time patients.',
     'perera@sunrisedental.lk', 'Mon, Wed, Fri — 9:00 AM to 4:00 PM'),
    ('Dr. Silva', 'Orthodontics', 'BDS (Peradeniya), MSc in Orthodontics',
     8, 'Dr. Silva specializes in braces, aligners, and bite correction for teens and adults. She trained in orthodontics after several years of general practice and enjoys planning long-term smile makeovers with her patients.',
     'silva@sunrisedental.lk', 'Tue, Thu, Sat — 10:00 AM to 5:00 PM');
INSERT INTO treatment_types (treatment_name, consultation_fee) VALUES
    ('Consultation', 1500.00),
    ('Tooth Filling', 4500.00),
    ('Tooth Extraction', 6000.00),
    ('Root Canal', 15000.00);
