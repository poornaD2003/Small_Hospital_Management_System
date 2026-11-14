package com.example.hospital_manager.service;


import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.MedicalRecordRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public Patient create(Patient patient) {
        patient.setId(null);
        return patientRepository.save(patient);
    }

    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Patient not found"
                ));
    }

    public List<Patient> search(String name) {
        return patientRepository.findByNameContainingIgnoreCase(name);
    }

    public Patient update(Long id, Patient details) {
        Patient patient = getById(id);

        patient.setName(details.getName());
        patient.setAge(details.getAge());
        patient.setGender(details.getGender());
        patient.setPhone(details.getPhone());
        patient.setEmail(details.getEmail());

        return patientRepository.save(patient);
    }

    public void delete(Long id) {
        Patient patient = getById(id);

        boolean hasAppointments =
                appointmentRepository.existsByPatientId(id);

        boolean hasRecords =
                medicalRecordRepository.existsByPatientId(id);

        if (hasAppointments || hasRecords) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Cannot delete patient with appointments or medical records"
            );
        }

        patientRepository.delete(patient);
    }
}
