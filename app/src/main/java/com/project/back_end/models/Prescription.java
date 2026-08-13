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
import org.springframework.data.mongodb.core.mapping.Document;

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

    // Constructors
    public Prescription() {}

    public Prescription(String patientName, Long appointmentId, String medication, String dosage, String doctorNotes, int refillCount, String pharmacyName) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
        this.refillCount = refillCount;
        this.pharmacyName = pharmacyName;
    }

    // Getters and Setters
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
}
