package com.example.hospital_manager.controller;


import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {

        return appointmentService.book(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public List<AppointmentResponse> doctorAppointments(
            Authentication authentication) {

        return appointmentService.getDoctorAppointments(
                authentication.getName()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentResponse> getAll() {
        return appointmentService.getAll();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public AppointmentResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return appointmentService.updateStatus(
                id,
                request,
                authentication.getName(),
                isAdmin
        );
    }
}