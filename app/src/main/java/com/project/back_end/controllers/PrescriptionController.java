package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

// Internal domain models and service layer paths
import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;
import com.project.back_end.services.AppointmentService;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final TokenService tokenService;
    private final AppointmentService appointmentService;
    
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, 
                                  TokenService tokenService, 
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.tokenService = tokenService;
        this.appointmentService = appointmentService;
    }
    
// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("${api.path}prescription")` to set the base path for all prescription-related endpoints.
//    - This controller manages creating and retrieving prescriptions tied to appointments.


// 2. Autowire Dependencies:
//    - Inject `PrescriptionService` to handle logic related to saving and fetching prescriptions.
//    - Inject the shared `Service` class for token validation and role-based access control.
//    - Inject `AppointmentService` to update appointment status after a prescription is issued.


// 3. Define the `savePrescription` Method:
//    - Handles HTTP POST requests to save a new prescription for a given appointment.
//    - Accepts a validated `Prescription` object in the request body and a doctor’s token as a path variable.
//    - Validates the token for the `"doctor"` role.
//    - If the token is valid, updates the status of the corresponding appointment to reflect that a prescription has been added.
//    - Delegates the saving logic to `PrescriptionService` and returns a response indicating success or failure.


// 4. Define the `getPrescription` Method:
//    - Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
//    - Accepts the appointment ID and a doctor’s token as path variables.
//    - Validates the token for the `"doctor"` role using the shared service.
//    - If the token is valid, fetches the prescription using the `PrescriptionService`.
//    - Returns the prescription details or an appropriate error message if validation fails.

    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Prescription prescription,
            @PathVariable("token") String token) {

        // Validate the path token context specifically matching the "doctor" role requirements
        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");
        if (!validationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Access Denied: Doctor authorization context required."));
        }

        try {
            // State Synchronization: Update appointment status to 1 (Completed) as per schema definitions
            appointmentService.changeStatus(prescription.getAppointmentId(), 1);

            // Delegate document persistence to the MongoDB service adapter layer
            ResponseEntity<?> savedPrescription = prescriptionService.savePrescription(prescription);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrescription);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Transaction Rollback: Unable to write prescription record: " + e.getMessage()));
        }
    }

    /**
     * 4. Define the getPrescription Method
     * Handles HTTP GET requests to fetch an electronic prescription by its relational appointment mapping key.
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("token") String token) {

        // Validate the path token context specifically matching the "doctor" role requirements
        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");
        if (!validationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Access Denied: Doctor authorization context required."));
        }

        try {
            ResponseEntity<?> prescription = prescriptionService.getPrescription(appointmentId);
            if (prescription == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Not Found: No prescription document matches appointment ID " + appointmentId));
            }
            return ResponseEntity.ok(prescription);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Data layer read exception occurred: " + e.getMessage()));
        }
    }
}


