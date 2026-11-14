package com.example.hospital_manager.dto;


public record UserResponse(
        Long id,
        String name,
        String username,
        String role
) {}
