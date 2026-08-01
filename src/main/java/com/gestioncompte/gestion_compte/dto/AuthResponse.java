package com.gestioncompte.gestion_compte.dto;

public class AuthResponse {

    private String token;
    private String accountNumber;

    public AuthResponse(String token, String accountNumber) {
        this.token = token;
        this.accountNumber = accountNumber;
    }

    public String getToken() {
        return token;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}