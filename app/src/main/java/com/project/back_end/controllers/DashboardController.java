package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

// Internal service tier import for validation
import com.project.back_end.services.TokenService; 

/**
 * DashboardController
 * Serves as the secure gatekeeper for server-side Thymeleaf dashboard views.
 * Validates role-based route access using path tokens before rendering HTML layouts.
 */
@Controller
public class DashboardController {

    private final TokenService tokenService;

    /**
     * Constructor injection for required token validation service dependency.
     */
    @Autowired
    public DashboardController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Secure endpoint mapping for the Admin Dashboard view layer.
     * Extracts token path variable and checks access permissions.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token, RedirectAttributes redirectAttrs) {
        Map<String, Object> validationResult = tokenService.validateToken(token, "admin");

        // FIX: Check for the absence of an error key or explicit success flag to permit access safely
        if (!validationResult.containsKey("error") && !validationResult.isEmpty()) {
            return "admin/adminDashboard";
        }

        // Capture the descriptive failure and pass it back to the landing view context
        String failureReason = validationResult.getOrDefault("error", "Invalid Session.").toString();
        redirectAttrs.addFlashAttribute("errorMessage", failureReason);

        return "redirect:http://localhost:8080";
    }

    /**
     * Secure endpoint mapping for the Doctor Dashboard view layer.
     * Extracts token path variable and checks access permissions.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token, RedirectAttributes redirectAttrs) {
        // Call validation service specifically requesting doctor structural checks
        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");

        // FIX: Check for the absence of an error key or explicit success flag to permit access safely
        if (!validationResult.containsKey("error") && !validationResult.isEmpty()) {
            return "doctor/doctorDashboard"; // Resolves to templates/doctor/doctorDashboard.html
        }

        // Secure Fallback: Force redirect back to base portal landing node with flash alerts
        String failureReason = validationResult.getOrDefault("error", "Invalid Session.").toString();
        redirectAttrs.addFlashAttribute("errorMessage", failureReason);
        return "redirect:http://localhost:8080";
    }
    
    /**
     * Secure endpoint mapping for the Patient Dashboard view layer.
     * FIX: Corrected validation check to ensure users with valid tokens are granted access, 
     * while invalid sessions are caught and rolled back via flash attributes.
     */
    @GetMapping("/patientDashboard/{token}")
    public String patientDashboard(@PathVariable("token") String token, RedirectAttributes redirectAttrs) {
        Map<String, Object> validationResult = tokenService.validateToken(token, "patient");

        // FIX: Changed from .isEmpty() to verifying that the claims map contains no errors and is populated
        if (!validationResult.containsKey("error") && !validationResult.isEmpty()) {
            return "patient/patientDashboard"; // Resolves to templates/patient/patientDashboard.html
        }

        // Secure Fallback: Force redirect back to base portal landing node if token is invalid
        String failureReason = validationResult.getOrDefault("error", "Invalid Session.").toString();
        redirectAttrs.addFlashAttribute("errorMessage", failureReason);
        return "redirect:http://localhost:8080";
    }
}
