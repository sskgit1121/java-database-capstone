package com.project.back_end.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Internal domain document model and repository tier imports
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

/**
 * PrescriptionService
 * Handles core business requirements for electronic prescriptions, ensuring duplicate checks,
 * relational status syncing coordination, and comprehensive try-catch exception handling boundaries.
 */
@Service
public class PrescriptionService {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);
    private final PrescriptionRepository prescriptionRepository;

    /**
     * 2. Constructor Injection for Dependencies
     * Injects the required MongoDB repository data access infrastructure component.
     */
    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * 3. savePrescription Method
     * Saves a new prescription to the NoSQL database after validating uniqueness per appointment.
     * Incorporates exception handling and appropriate error status responses.
     * 
     * @param prescription The prescription document payload to save
     * @return ResponseEntity mapping the transactional outcome status and payload metadata
     */
    public ResponseEntity<?> savePrescription(Prescription prescription) {
        try {
            // Before saving, check if a prescription already exists for the same appointment ID
            List<Prescription> existingPrescriptions = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());

            if (existingPrescriptions != null && !existingPrescriptions.isEmpty()) {
                // 3. If a prescription exists, returns a 400 Bad Request with a clear message
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("success", false);
                errorMap.put("message", "Bad Request: A prescription has already been issued and recorded for this appointment ID.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
            }

            // If no prescription exists, saves the new prescription document
            Prescription savedPrescription = prescriptionRepository.save(prescription);

            // 3. Returns a 201 Created status with a success message payload
            Map<String, Object> successMap = new HashMap<>();
            successMap.put("success", true);
            successMap.put("message", "Prescription created and recorded successfully.");
            successMap.put("prescription", savedPrescription);
            return ResponseEntity.status(HttpStatus.CREATED).body(successMap);

        } catch (Exception e) {
            // 5. Exception Handling: Logs the error and returns an HTTP 500 Internal Server Error
            logger.error("Exception encountered while saving prescription for appointment ID {}: ", prescription.getAppointmentId(), e);
            Map<String, Object> failureMap = new HashMap<>();
            failureMap.put("success", false);
            failureMap.put("error", "Internal Server Error: Failed to successfully write prescription data.");
            failureMap.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failureMap);
        }
    }

    /**
     * 4. getPrescription Method
     * Retrieves a prescription document associated with a specific appointment ID.
     * Incorporates defensive edge-case tracking and custom map mapping responses.
     * 
     * @param appointmentId The unique relational reference ID mapping lookup
     * @return ResponseEntity wrapping the matched document layout or structural exception errors
     */
    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);

            // Handle edge case where no prescriptions are found for the given appointment reference
            if (prescriptions == null || prescriptions.isEmpty()) {
                Map<String, Object> notFoundMap = new HashMap<>();
                notFoundMap.put("success", false);
                notFoundMap.put("message", "Not Found: No prescription entries exist for the provided appointment ID.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundMap);
            }

            // 4. Returns the prescription within a map wrapped in a 200 OK status
            Map<String, Object> successMap = new HashMap<>();
            successMap.put("success", true);
            successMap.put("prescription", prescriptions.get(0)); // Returns the primary issued prescription document
            return ResponseEntity.status(HttpStatus.OK).body(successMap);

        } catch (Exception e) {
            // 5. Exception Handling: Logs the error and returns an HTTP 500 Internal Server Error
            logger.error("Exception encountered while fetching prescription for appointment ID {}: ", appointmentId, e);
            Map<String, Object> failureMap = new HashMap<>();
            failureMap.put("success", false);
            failureMap.put("error", "Internal Server Error: Failed to retrieve prescription records.");
            failureMap.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failureMap);
        }
    }
}
