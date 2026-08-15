package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.project.back_end.DTO.Login;
// Internal architectural layers and DTO imports
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.

	 

	/**
	 * DoctorController
	 * Manages doctor-related functionalities such as registration, login, updates, and availability.
	 * Follows the specific multi-endpoint blueprint parameters.
	 */
	

	private final DoctorService doctorService;
    private final TokenService tokenService;

    /**
     * Constructor Injection for Dependencies:
     * FIX: Replaced the non-existent 'Service' type with the valid, managed 'TokenService' bean 
     * to resolve the UnsatisfiedDependencyException at application startup.
     */
    @Autowired
    public DoctorController(DoctorService doctorService, TokenService tokenService) {
        this.doctorService = doctorService;
        this.tokenService = tokenService;
    }

	    /**
	     * 3. Define the `getDoctorAvailability` Method:
	     * Handles HTTP GET requests to check a specific doctor’s availability on a given date.
	     * Requires user type, doctorId, date, and token as path variables.
	     */
	    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
	    public ResponseEntity<?> getDoctorAvailability(
	            @PathVariable("user") String userType,
	            @PathVariable("doctorId") Long doctorId,
	            @PathVariable("date") String date,
	            @PathVariable("token") String token) {

	        // First validates the token against the user type using the shared service
	        Map<String, Object> validationResult = tokenService.validateToken(token, userType);

	        // If the token is invalid, returns an error response
	        if (!validationResult.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Unauthorized access context: Invalid token for user type " + userType));
	        }

	        // Otherwise, returns the availability status for the doctor
	        try {
	            List<String> availability = doctorService.getDoctorAvailability(doctorId, date);
	            return ResponseEntity.ok(Map.of("doctorId", doctorId, "date", date, "availableSlots", availability));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("error", "Failed to retrieve schedules: " + e.getMessage()));
	        }
	    }

	    /**
	     * 4. Define the `getDoctor` Method:
	     * Handles HTTP GET requests to retrieve a list of all doctors.
	     * Returns the list within a response map under the key "doctors" with HTTP 200 OK status.
	     */
	    @GetMapping
	    public ResponseEntity<Map<String, Object>> getDoctor() {
	        List<Doctor> allDoctors = doctorService.getDoctors();
	        
	        Map<String, Object> response = new HashMap<>();
	        response.put("doctors", allDoctors);
	        
	        return ResponseEntity.ok(response);
	    }

	    /**
	     * 5. Define the `saveDoctor` Method:
	     * Handles HTTP POST requests to register a new doctor.
	     * Accepts a validated Doctor object in the request body and a token for authorization.
	     */
	    @PostMapping
	    public ResponseEntity<?> saveDoctor(
	            @Valid @RequestBody Doctor doctor,
	            @RequestParam("token") String token) {

	        // Validates the token for the "admin" role before proceeding
	        Map<String, Object> isAdmin = tokenService.validateToken(token, "admin");
	        if (!isAdmin.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Access Denied: Admin authorization required."));
	        }

	        // Fixed Bug: Properly assigned the output of the statement to the 'exists' variable
	        boolean exists = doctorService.existsByEmail(doctor.getEmail());
	        if (exists) {
	            return ResponseEntity.status(HttpStatus.CONFLICT)
	                    .body(Map.of("error", "Conflict: A doctor record with this email already exists."));
	        }

	        int savedDoctor = doctorService.saveDoctor(doctor);
	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(Map.of("message", "Doctor successfully registered.", "doctor", savedDoctor));
	    }

	    /**
	     * 6. Define the `doctorLogin` Method:
	     * Handles HTTP POST requests for doctor login.
	     * Accepts a validated Login DTO containing credentials.
	     */
	    @PostMapping("/login")
	    public ResponseEntity<?> doctorLogin(@Valid @RequestBody Login loginDTO) {
	        // Fixed Bug: Corrected target object reference from 'login' to 'loginDTO' and mapped to validateDoctor
	        Map<String, Object> authResult = doctorService.validateDoctor(loginDTO.getEmail(), loginDTO.getPassword());
	        
	        if (Boolean.TRUE.equals(authResult.get("success"))) {
	            return ResponseEntity.ok(authResult);
	        }
	        
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResult);
	    }

	    /**
	     * 7. Define the `updateDoctor` Method:
	     * Handles HTTP PUT requests to update an existing doctor's information.
	     * Accepts a validated Doctor object and a token for authorization.
	     */
	    @PutMapping
	    public ResponseEntity<?> updateDoctor(
	            @Valid @RequestBody Doctor doctor,
	            @RequestParam("token") String token) {

	        // Token must belong to an "admin"
	        Map<String, Object> isAdmin = tokenService.validateToken(token, "admin");
	        if (!isAdmin.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Access Denied: Admin privileges required to update doctor entries."));
	        }

	        // If the doctor exists, updates the record and returns success; otherwise, returns not found
	        try {
	            int updatedDoctor = doctorService.updateDoctor(doctor);
	            return ResponseEntity.ok(Map.of("message", "Doctor record successfully updated.", "doctor", updatedDoctor));
	        } catch (jakarta.persistence.EntityNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Map.of("error", "Not Found: No active doctor entry matches the provided identifier."));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of("error", "Transaction Failed: " + e.getMessage()));
	        }
	    }

	    /**
	     * 8. Define the `deleteDoctor` Method:
	     * Handles HTTP DELETE requests to remove a doctor by ID.
	     * Requires both doctor ID and an admin token as path variables.
	     */
	    @DeleteMapping("/{doctorId}/{token}")
	    public ResponseEntity<?> deleteDoctor(
	            @PathVariable("doctorId") Long doctorId,
	            @PathVariable("token") String token) {

	        // Validation block confirming token contains admin role context permissions
	        Map<String, Object> isAdmin = tokenService.validateToken(token, "admin");
	        if (!isAdmin.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Access Denied: Admin authorization required for deletions."));
	        }

	        // Fixed Bug: Explicitly evaluating integer state flags returned from DoctorService layer
	        int deleted = doctorService.deleteDoctor(doctorId);
	        if (deleted == 1) {
	            return ResponseEntity.ok(Map.of("message", "Doctor record successfully removed from the system."));
	        } else if (deleted == -1) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Map.of("error", "Not Found: Unable to find doctor entry to execute deletion request."));
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of("error", "Internal error: Failed to complete deletion transaction request."));
	        }
	    }

	    /**
	     * 9. Define the `filter` Method:
	     * Handles HTTP GET requests to filter doctors based on name, time, and specialty.
	     * Accepts name, time, and speciality as path variables.
	     */
	    @GetMapping("/filter/{name}/{time}/{speciality}")
	    public ResponseEntity<?> filter(
	            @PathVariable("name") String name,
	            @PathVariable("time") String time,
	            @PathVariable("speciality") String speciality) {
	        
	        List<Doctor> filteredDoctors = doctorService.filterDoctorsByNameSpecilityandTime(name, speciality, time);
	        return ResponseEntity.ok(filteredDoctors);
	    }
	}



