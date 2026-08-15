package com.project.back_end.models;

import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ==========================================
 * 5. PRESCRIPTION MODEL (MongoDB Document Object)
 * ==========================================
 */
@Document(collection = "prescriptions")
public class Prescription {

    @org.springframework.data.annotation.Id
    private String id;

    @NotNull(message = "Patient name is required")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;

    @NotNull(message = "Appointment ID reference is required")
    private Long appointmentId;

    @NotNull(message = "Medication name is required")
    @Size(min = 3, max = 100, message = "Medication name must be between 3 and 100 characters")
    private String medication;

    @NotNull(message = "Dosage information is required")
    @Size(min = 3, max = 20, message = "Dosage must be between 3 and 20 characters")
    private String dosage;

    @Size(max = 200, message = "Doctor notes cannot exceed 200 characters")
    private String doctorNotes;

    // Advanced Enhanced Fields
    @Min(value = 0, message = "Refill count cannot be negative")
    private int refillCount;

    @Size(min = 3, max = 100, message = "Pharmacy name must be between 3 and 100 characters")
    private String pharmacyName;

    // ==========================================
    // EXTENDED FIELDS (Added to fulfill MongoDB Schema Design without removing original fields)
    // ==========================================
    private Long patientId;
    private int ageAtVisit;
    
    private Long doctorId;
    private String doctorFullName;
    private String specialization;
    
    private LocalDateTime issuedDate = LocalDateTime.now();
    private List<String> diagnoses;
    
    private String frequency;
    private int durationDays;
    
    private String pharmacyLocationId;
    private String pharmacyAddress;
    
    private String schemaVersion = "2.0";
    private String digitalSignatureId;
    private List<String> tags;

    // Constructors
    public Prescription() {}

    // Original Constructor preserved exactly
    public Prescription(String patientName, Long appointmentId, String medication, String dosage, String doctorNotes, int refillCount, String pharmacyName) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
        this.refillCount = refillCount;
        this.pharmacyName = pharmacyName;
    }

    // Extended Constructor including all fields
    public Prescription(String id, String patientName, Long appointmentId, String medication, String dosage, String doctorNotes, 
                        int refillCount, String pharmacyName, Long patientId, int ageAtVisit, Long doctorId, String doctorFullName, 
                        String specialization, LocalDateTime issuedDate, List<String> diagnoses, String frequency, int durationDays, 
                        String pharmacyLocationId, String pharmacyAddress, String schemaVersion, String digitalSignatureId, List<String> tags) {
        this.id = id;
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
        this.refillCount = refillCount;
        this.pharmacyName = pharmacyName;
        this.patientId = patientId;
        this.ageAtVisit = ageAtVisit;
        this.doctorId = doctorId;
        this.doctorFullName = doctorFullName;
        this.specialization = specialization;
        this.issuedDate = issuedDate;
        this.diagnoses = diagnoses;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.pharmacyLocationId = pharmacyLocationId;
        this.pharmacyAddress = pharmacyAddress;
        this.schemaVersion = schemaVersion;
        this.digitalSignatureId = digitalSignatureId;
        this.tags = tags;
    }

    // Original Getters and Setters preserved exactly
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }

    public int getRefillCount() {
        return refillCount;
    }

    public void setRefillCount(int refillCount) {
        this.refillCount = refillCount;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    // Extended Getters and Setters
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public int getAgeAtVisit() { return ageAtVisit; }
    public void setAgeAtVisit(int ageAtVisit) { this.ageAtVisit = ageAtVisit; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorFullName() { return doctorFullName; }
    public void setDoctorFullName(String doctorFullName) { this.doctorFullName = doctorFullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }

    public List<String> getDiagnoses() { return diagnoses; }
    public void setDiagnoses(List<String> diagnoses) { this.diagnoses = diagnoses; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public String getPharmacyLocationId() { return pharmacyLocationId; }
    public void setPharmacyLocationId(String pharmacyLocationId) { this.pharmacyLocationId = pharmacyLocationId; }

    public String getPharmacyAddress() { return pharmacyAddress; }
    public void setPharmacyAddress(String pharmacyAddress) { this.pharmacyAddress = pharmacyAddress; }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }

    public String getDigitalSignatureId() { return digitalSignatureId; }
    public void setDigitalSignatureId(String digitalSignatureId) { this.digitalSignatureId = digitalSignatureId; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
