package com.project.back_end.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

// Mock import path for your validation service layer
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

        if (validationResult.isEmpty()) {
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
    public String doctorDashboard(@PathVariable("token") String token) {
        // Call validation service specifically requesting doctor structural checks
        Map<String, Object> validationResult = tokenService.validateToken(token, "doctor");

        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard"; // Resolves to templates/doctor/doctorDashboard.html
        }

        // Secure Fallback: Force redirect back to base portal landing node
        return "redirect:http://localhost:8080";
    }
}

