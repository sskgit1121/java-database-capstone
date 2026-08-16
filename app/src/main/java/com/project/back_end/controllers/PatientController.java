package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import com.project.back_end.DTO.Login;
// Internal domain models and service layer components
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;

/**
 * PatientController
 * Handles incoming REST API requests related to patient profiles, registration operations,
 * and security-validated appointment history lookups.
 */
@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final TokenService tokenService;

    /**
     * Constructor Injection for Dependencies
     * Ensures proper decoupling and type-checking during application context loading.
     */
    @Autowired
    public PatientController(PatientService patientService, TokenService tokenService) {
        this.patientService = patientService;
        this.tokenService = tokenService;
    }

    /**
     * Handles HTTP POST requests to register a new patient profile.
     * Maps incoming validated JSON requests to the service tier.
     * 
     * @param patient the validated incoming patient payload mapping
     * @return a structured response entity tracking operational completion
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody Patient patient) {
        int result = patientService.createPatient(patient);
        
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Patient profile successfully registered."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Transaction Rollback: Unable to write patient record due to a system error."));
        }
    }

    /**
     * Handles HTTP GET requests to fetch appointment histories for a specific patient.
     * Secures access by enforcing a valid Bearer token bound to the patient role profile.
     * 
     * @param patientId the target identity locator path parameter
     * @param tokenHeader the incoming Authorization header metadata context string
     * @return a structured collection of appointment records or an explicit error entity
     */
    @GetMapping("/{patientId}/appointments")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable("patientId") Long patientId,
            @RequestHeader("Authorization") String tokenHeader) {
        
        // Enforce structural format validation checks on the inbound bearer token parameter
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Access Denied: Missing or malformed Authorization header metadata context."));
        }
        
        String token = tokenHeader.substring(7);
        
        // Cross-examine security access context signatures via TokenService
        Map<String, Object> tokenValidation = tokenService.validateToken(token, "patient");
        if (!tokenValidation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Access Denied: The provided token context is invalid or expired for this request route."));
        }

        // Fetch the mapped DTO records from the service layer tier
        List<PatientService.AppointmentDTO> appointments = patientService.getPatientAppointment(patientId);
        return ResponseEntity.ok(appointments);
    }
    
    /**
     * Handles HTTP POST requests to authenticate a patient profile.
     * Aligned with AdminController pattern to use the Model class instead of a loose map wrapper.
     * 
     * @param patient the model entity instance holding credential inputs
     * @return a structured response tracking session token distribution
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@RequestBody Login loginDTO) {
        // Delegates authentication logic to the service layer using entity property getters
        Map<String, Object> authResult = tokenService.validateToken(loginDTO.getEmail(), loginDTO.getPassword());

        // Check outcome status parameters to determine accurate HTTP delivery mapping
        if (Boolean.TRUE.equals(authResult.get("success"))) {
            return ResponseEntity.ok(authResult);
        }

        // Returns an HTTP 401 Unauthorized status context for bad credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResult);
    }

}
