package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.dto.AuthResponse;
import com.gestioncompte.gestion_compte.dto.LoginRequest;
import com.gestioncompte.gestion_compte.dto.RegisterRequest;
import com.gestioncompte.gestion_compte.model.Account;
import com.gestioncompte.gestion_compte.model.Client;
import com.gestioncompte.gestion_compte.repository.ClientRepository;
import com.gestioncompte.gestion_compte.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private ClientRepository clientRepository;
    private AccountService accountService;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        accountService = mock(AccountService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(clientRepository, accountService, passwordEncoder, jwtService);
    }

    @Test
    void registerThrowsWhenEmailAlreadyUsed() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Jane Doe");
        request.setEmail("jane@example.com");
        request.setPassword("password123");
        when(clientRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(clientRepository, never()).save(any());
        verify(accountService, never()).createAccount(any());
    }

    @Test
    void registerGeneratesValidToken() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Jane Doe");
        request.setEmail("jane@example.com");
        request.setPassword("password123");
        when(clientRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Account account = new Account();
        when(accountService.createAccount(any(Client.class))).thenReturn(account);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(account.getAccountNumber(), response.getAccountNumber());
    }

    @Test
    void loginThrowsOnWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jane@example.com");
        request.setPassword("wrong-password");
        Client client = new Client("Jane Doe", "jane@example.com", "hashed-password");
        when(clientRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginGeneratesValidToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jane@example.com");
        request.setPassword("password123");
        Client client = new Client("Jane Doe", "jane@example.com", "hashed-password");
        when(clientRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");
        when(accountService.listAccountsForClient(client.getId())).thenReturn(List.of());

        AuthResponse response = authService.login(request);

        assertEquals("fake-jwt-token", response.getToken());
    }
}
