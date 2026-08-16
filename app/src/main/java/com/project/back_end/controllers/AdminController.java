package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.Map;

import com.project.back_end.DTO.Login;
// Internal domain models and service tier imports
import com.project.back_end.models.Admin;
import com.project.back_end.services.TokenService;

/**
 * 1. Set Up the Controller Class:
 * Annotated with @RestController to handle web requests and return JSON responses.
 * Uses RequestMapping with property placeholder interpolation to define a configurable base path.
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final TokenService tokenService;

    /**
     * 2. Autowire Service Dependency:
     * Uses clean constructor injection to autowire the token and authentication management service.
     */
    @Autowired
    public AdminController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 3. Define the adminLogin Method:
     * Handles incoming HTTP POST requests for administrative authentication.
     * Accepts a validated Admin credential object inside the JSON request body.
     */
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody Login loginDto) {
        // Delegates authentication logic to the validateAdmin method in the service layer
        Map<String, Object> authResult = tokenService.validateAdmin(loginDto.getEmail(), loginDto.getPassword());

        // Checks the outcome flag returned from the business tier to determine response codes
        if (Boolean.TRUE.equals(authResult.get("success"))) {
            return ResponseEntity.ok(authResult);
        }

        // Returns an HTTP 401 Unauthorized status headers mapping context for bad logins
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResult);
    }
}
