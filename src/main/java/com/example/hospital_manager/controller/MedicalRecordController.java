package com.example.hospital_manager.controller;


import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.service.MedicalRecordService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    public MedicalRecordController(
            MedicalRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse create(
            @Valid @RequestBody MedicalRecordRequest request,
            Authentication authentication) {

        return recordService.create(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public List<MedicalRecordResponse> getPatientRecords(
            @PathVariable Long patientId) {

        return recordService.getPatientRecords(patientId);
    }
}