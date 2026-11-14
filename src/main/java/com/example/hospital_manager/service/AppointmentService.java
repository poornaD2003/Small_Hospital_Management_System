package com.example.hospital_manager.service;


import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public AppointmentResponse book(
            AppointmentRequest request,
            String currentUsername) {

        AppUser currentUser = userRepository
                .findByUsername(currentUsername)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Patient not found"
                ));

        // A patient may only book for their own patient profile.
        if (currentUser.getRole() == Role.PATIENT) {
            // For this simple version, the username must equal
            // the patient's email or be linked in your data model.
            // Use the linked patient-user relationship in production.
            if (patient.getEmail() == null ||
                    !patient.getEmail().equalsIgnoreCase(currentUsername)) {
                throw new ApiException(
                        HttpStatus.FORBIDDEN,
                        "You can only book appointments for your own profile"
                );
            }
        }

        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Doctor not found"
                ));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Selected user is not a doctor"
            );
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getDoctorAppointments(
            String username) {

        AppUser doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Doctor not found"
                ));

        return appointmentRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponse> getAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse updateStatus(
            Long id,
            StatusRequest request,
            String username,
            boolean isAdmin) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "Appointment not found"
                ));

        if (!isAdmin &&
                !appointment.getDoctor().getUsername().equals(username)) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own appointments"
            );
        }

        appointment.setStatus(request.status());

        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDate(),
                appointment.getStatus().name()
        );
    }
}