package com.example.hospital_manager.dto;


import com.example.hospital_manager.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(

        @NotBlank
        String name,

        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotNull
        Role role
) {}