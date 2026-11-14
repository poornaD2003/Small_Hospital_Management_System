package com.example.hospital_manager.controller;


import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@Valid @RequestBody Patient patient) {
        return patientService.create(patient);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Patient> getAll() {
        return patientService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Patient getById(@PathVariable Long id) {
        return patientService.getById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Patient> search(@RequestParam String name) {
        return patientService.search(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Patient update(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient) {

        return patientService.update(id, patient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}