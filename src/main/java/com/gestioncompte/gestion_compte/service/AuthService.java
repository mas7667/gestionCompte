package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.dto.AuthResponse;
import com.gestioncompte.gestion_compte.dto.LoginRequest;
import com.gestioncompte.gestion_compte.dto.RegisterRequest;
import com.gestioncompte.gestion_compte.model.Account;
import com.gestioncompte.gestion_compte.model.Client;
import com.gestioncompte.gestion_compte.repository.ClientRepository;
import com.gestioncompte.gestion_compte.security.JwtService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final ClientRepository clientRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ClientRepository clientRepository, AccountService accountService,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account already exists with this email.");
        }

        Client client = new Client(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        client = clientRepository.save(client);

        Account account = accountService.createAccount(client);

        String token = jwtService.generateToken(
                new User(client.getEmail(), client.getPasswordHash(), Collections.emptyList()));

        return new AuthResponse(token, account.getAccountNumber());
    }

    public AuthResponse login(LoginRequest request) {
        Client client = clientRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        if (!passwordEncoder.matches(request.getPassword(), client.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        String token = jwtService.generateToken(
                new User(client.getEmail(), client.getPasswordHash(), Collections.emptyList()));

        String firstAccountNumber = accountService.listAccountsForClient(client.getId())
                .stream().findFirst().map(Account::getAccountNumber).orElse(null);

        return new AuthResponse(token, firstAccountNumber);
    }
}