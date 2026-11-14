package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.RegisterRequest;
import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.service.AuthService;
import com.example.hospital_manager.exception.ApiException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StaffController {

    private final AuthService authService;

    public StaffController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return authService.getAllUsers();
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN','PATIENT')")
    public List<UserResponse> getDoctors() {
        return authService.getDoctors();
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createStaff(
            @Valid @RequestBody RegisterRequest request) {

        if (request.role() == Role.PATIENT) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Use patient registration for patient accounts");
        }

        return authService.register(request);
    }
}
