package com.gestioncompte.gestion_compte.controller;

import com.gestioncompte.gestion_compte.dto.AuthResponse;
import com.gestioncompte.gestion_compte.dto.LoginRequest;
import com.gestioncompte.gestion_compte.dto.RegisterRequest;
import com.gestioncompte.gestion_compte.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}