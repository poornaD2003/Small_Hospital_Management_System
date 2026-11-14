package com.example.hospital_manager.service;


import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hospital_manager.exception.ApiException;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        AppUser user = new AppUser();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setEnabled(true);

        AppUser saved = userRepository.save(user);

        return toUserResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        UserDetails details =
                (UserDetails) authentication.getPrincipal();

        AppUser user = userRepository.findByUsername(details.getUsername())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getRole().name()
        );
    }

    public UserResponse getMe(String username) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        return toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    public List<UserResponse> getDoctors() {
        return userRepository.findByRole(Role.DOCTOR)
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    private UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}