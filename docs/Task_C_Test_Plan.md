# Task C: Test Plan and TDD Implementation

## Rationale for the Approach Adopted

A Test-Driven Development (TDD) approach was adopted for the Sunrise Dental
Clinic system because it handles data that directly affects patient safety
and clinic billing accuracy (appointment scheduling, treatment fees, staff
account access). Each unit — model, DAO, and service — was given a clearly
verified contract via tests before being relied on by the layer above it.

## How TDD Was Applied

The Red-Green-Refactor cycle was followed for each unit: a test was written
first against the intended method signature, the minimum implementation
needed to pass it was then written, and the implementation was refactored
afterwards while re-running the tests to confirm behaviour was preserved.
This was applied most directly to `BillingService`, where a duplicate-bill
guard was added specifically because a test (`testGenerateBill_CalledTwice_
DoesNotDuplicate`) demonstrated that calling generateBill twice for the same
appointment created two separate bill rows before the fix.

## Test Environment
- IDE: Eclipse IDE (Dynamic Web Project, WebContent layout)
- Testing Framework: JUnit 5 (via Eclipse's built-in JUnit 5 library)
- Application Server: Apache Tomcat 10.1
- Database: MySQL / MariaDB (via XAMPP)

## Test Data
| Field | Sample Value | Purpose |
|---|---|---|
| Patient ID | PT#### (auto-generated) | Verify ID format |
| Appointment number | APT#### (auto-generated) | Verify ID format |
| Treatment ID 1 (Consultation) | Fee = 1500.00 | Verify billing accuracy |
| Invalid appointment number | APT9999 | Verify graceful null handling |
| Missing name/contact | "" | Verify AppointmentService rejects incomplete input |
| Login: admin/admin123 (ACTIVE) | Seeded staff row | Verify authentication success |
| Login: admin/wrongpassword | — | Verify authentication failure |
| Disabled staff account | status='DISABLED' | Verify a disabled account cannot log in |
| Password reset token | Generated via createPasswordResetToken() | Verify the full reset lifecycle |

## Test Plan by Layer

**Model tests** (`PatientTest`, `AppointmentTest`, `BillTest`,
`TreatmentTypeTest`, `StaffTest`) — verify constructors, getters/setters,
and the `isAdmin()` role-check logic hold correct values with no
transformation errors.

**DAO tests** (`PatientDAOTest`, `AppointmentDAOTest`, `TreatmentTypeDAOTest`,
`BillDAOTest`, `StaffDAOTest`) — verify each method against the live schema:
save-then-find round trips, ID-generation format, correct null handling for
non-existent records, the full password-reset token lifecycle (create →
validate → consume), and that disabling a staff account actually blocks
login.

**Service tests** (`AppointmentServiceTest`, `BillingServiceTest`,
`NotificationServiceTest`) — verify the business rules above the DAO layer:
new-patient auto-registration, input validation, the duplicate-bill guard,
and the Strategy-pattern notification system (every channel is invoked, and
one channel failing never blocks the others or the registration itself).

## Use of Test Automation
All tests run through JUnit 5 directly in Eclipse (right-click the `test`
folder → Run As → JUnit Test runs every test class in one pass), which is
the automation layer supporting the TDD cycle described above.

## Evaluating Overall Success or Failure
Passing tests confirm: IDs are generated in the correct format, incomplete
registrations are rejected before reaching the database, non-existent
records are handled gracefully, billed amounts match the treatment's actual
fee, a bill is never duplicated on repeated calls, disabled accounts cannot
authenticate, and the password-reset token lifecycle behaves correctly end
to end. A failing test pinpoints exactly which layer is responsible.

## Requirement Traceability

| Requirement | Verified By |
|---|---|
| User Authentication (Login) | `StaffDAOTest.testValidateLogin_ValidCredentials()`, `testValidateLogin_InvalidPassword()` |
| Disabled accounts cannot log in | `StaffDAOTest.testSetStaffStatus_DisablesAccountLogin()` |
| Register New Appointment | `AppointmentServiceTest.testRegisterAppointment_NewPatient_Succeeds()`, `AppointmentDAOTest.testSaveAndFindAppointment()` |
| Input validation on registration | `AppointmentServiceTest.testRegisterAppointment_MissingRequiredFields_ReturnsNull()` |
| Display Appointment Details (search) | `AppointmentDAOTest.testFindByNumber_NonExistentAppointment()` |
| Calculate and Print Bill | `BillingServiceTest.testGenerateBill_ForValidAppointment_CalculatesCorrectFee()` |
| No duplicate billing | `BillingServiceTest.testGenerateBill_CalledTwice_DoesNotDuplicate()` |
| Notification (email/SMS confirmation) | `NotificationServiceTest.testSendAppointmentConfirmation_CallsAllChannels()`, `testSendAppointmentConfirmation_OneChannelFailing_DoesNotStopOthers()` |
| Admin-only staff management | `StaffDAOTest.testAddStaff_And_GetAllStaff()` |
| Password reset (forgot password flow) | `StaffDAOTest.testPasswordResetFlow_TokenLifecycle()` |

*(For submission, add screenshots of the green JUnit test-runner results in
Eclipse here — an explicit "Excellent" band requirement.)*
