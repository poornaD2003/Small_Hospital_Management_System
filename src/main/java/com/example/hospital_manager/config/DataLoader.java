package com.example.hospital_manager.config;


import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                AppUser admin = new AppUser();
                admin.setName("System Administrator");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin@gmail.com"));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            if (!userRepository.existsByUsername("doctor1")) {

                AppUser doctor = new AppUser();
                doctor.setName("Dr. Kamal");
                doctor.setUsername("doctor1");
                doctor.setPassword(passwordEncoder.encode("doctor1@gmail.com"));
                doctor.setRole(Role.DOCTOR);

                userRepository.save(doctor);
            }

            if (!userRepository.existsByUsername("patient1")) {

                AppUser patientUser = new AppUser();
                patientUser.setName("Test Patient");
                patientUser.setUsername("patient1");
                patientUser.setPassword(
                        passwordEncoder.encode("patient1@gmail.com")
                );
                patientUser.setRole(Role.PATIENT);

                userRepository.save(patientUser);
            }

            if (patientRepository.count() == 0) {

                Patient patient = new Patient();
                patient.setName("Test Patient");
                patient.setAge(25);
                patient.setGender("Male");
                patient.setPhone("0771234567");
                patient.setEmail("patient1@example.com");

                patientRepository.save(patient);
            }
        };
    }
}