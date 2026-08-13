Smart Clinic Management System - Schema Design
This document details the hybrid database design for the Smart Clinic Management System. It leverages MySQL for structured, relational, transactional data (such as user accounts, roles, and schedules) and MongoDB for flexible, unstructured, or document-centric data (such as electronic prescriptions, medical summaries, and dynamic metadata).
--------------
##MySQL Database Design
The relational database ensures structural integrity, ACID compliance, and clear relational mappings across the core operating entities of the clinic.
----
1. Table: patients
Stores core personal and contact details for individuals registered at the clinic.
id: INT, Primary Key, Auto Increment, Uniquefirst_name: VARCHAR(50), Not Nulllast_name: VARCHAR(50), Not Nullemail: VARCHAR(100), Not Null, Unique  (Format validated via application code)phone: VARCHAR(15), Not Null, Unique (Used for SMS alerts and identity queries)date_of_birth: DATE, Not Nullgender: VARCHAR(10), Not Nullcreated_at: TIMESTAMP, Default Current_TimestampDesign Choices & Constraints:Email & Phone Unique Constraints: Prevents duplicate accounts.Deletions: If a patient profile is deleted, business rules dictate a Soft Delete pattern (e.g., adding an is_active boolean column) rather than a hard cascade, ensuring historical appointment and financial records are retained forever for legal compliance.

2. Table: doctorsTracks medical staff records, specializations, and essential operational contexts.
id: INT, Primary Key, Auto Increment, Uniquefirst_name: VARCHAR(50), Not Nulllast_name: VARCHAR(50), Not Nullemail: VARCHAR(100), Not Null, Uniquephone: VARCHAR(15), Not Null, Uniquespecialization: VARCHAR(100), Not Nullworking_hours_start: TIME, Not Nullworking_hours_end: TIME, Not Nullcreated_at: TIMESTAMP, Default Current_Timestamp

Design Choices & Constraints:
Time Window Verification: Working hour limits are strictly enforced at the application/service layer to ensure scheduling attempts fall within standard shifts.

3. Table: appointmentsManages transactional scheduling data connecting patients and doctors.id: INT, Primary Key, Auto Increment, Uniquedoctor_id: INT, Foreign Key → doctors(id), On Delete Restrictpatient_id: INT, Foreign Key → patients(id), On Delete Cascadeappointment_time: DATETIME, Not Nullduration_minutes: INT, Not Null, Default 30status: INT, Not Null (0 = Scheduled, 1 = Completed, 2 = Cancelled, 3 = No Show)

Design Choices & Constraints:Overlapping Appointments: Prevented via database indexing combination and Service Layer Validation before executing an INSERT statement.On Delete Restrict (Doctors): A doctor profile cannot be entirely deleted if they have upcoming or unresolved operational appointments linked to them.

4. Table: adminContains internal administrative account details used for system config and dashboard access management.id: INT, Primary Key, Auto Increment, Uniqueusername: VARCHAR(50), Not Null, Uniqueemail: VARCHAR(100), Not Null, Uniquepassword_hash: VARCHAR(255), Not Null (Salted and cryptographically hashed via Spring Security)role: VARCHAR(20), Not Null, Default 'ROLE_ADMIN'created_at: TIMESTAMP, Default Current_Timestamp
++++++++++++++++++
##MongoDB Collection Design
Unstructured data elements, rich textual prescriptions, metadata attachments, and rapidly expanding audit histories fit efficiently into flexible, schema-less NoSQL structures.
-----
Collection: prescriptionsPrescriptions exist as an evolution of a finished appointment but remain structurally independent documents. Storing them in MongoDB permits complex arrays of medications, customized intake fields, and unstructured doctor notes that differ widely between specialties 
(e.g., General Medicine vs. Pediatrics).json{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientDetails": {
    "patientId": 1042,
    "fullName": "John Smith",
    "ageAtVisit": 34
  },
  "doctorDetails": {
    "doctorId": 8,
    "fullName": "Dr. Sarah Jenkins",
    "specialization": "Cardiology"
  },
  "issuedDate": "2026-08-13T10:30:00Z",
  "diagnoses": ["Essential Hypertension", "Mild Vitamin D Deficiency"],
  "medications": [
    {
      "name": "Lisinopril",
      "dosage": "10mg",
      "frequency": "Once daily in the morning",
      "durationDays": 90,
      "refillCount": 3
    },
    {
      "name": "Vitamin D3",
      "dosage": "2000 IU",
      "frequency": "Once daily with food",
      "durationDays": 30,
      "refillCount": 0
    }
  ],
  "doctorNotes": "Patient should monitor blood pressure daily at home. Return for follow-up blood work if readings exceed 140/90 consistently.",
  "pharmacyRouting": {
    "preferredPharmacyName": "Walgreens SF",
    "locationId": "W-94103",
    "address": "123 Market Street, San Francisco, CA"
  },
  "metadata": {
    "schemaVersion": "2.0",
    "digitalSignatureId": "sig_d92f811a4c",
    "tags": ["CardioCare", "ChronicManagement"]
  }
}
Design Choices & Structural Justifications:
Hybrid Data Duplication (Denormalization): Including standard descriptive text arrays like patientDetails.fullName and doctorDetails.fullName minimizes the requirement to execute continuous cross-tier JOIN queries when compiling common medical summary dashboards.
Embedded Arrays: Medications are wrapped within a clear JSON array layout directly inside the document block, keeping all specific items ordered together safely.
Schema Evolution Support: Incorporating a metadata.schemaVersion key guarantees the backend app layers can easily interpret, process, or dynamically alter historic structures without experiencing system crashes.
