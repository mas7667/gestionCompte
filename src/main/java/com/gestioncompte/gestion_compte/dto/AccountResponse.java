package com.gestioncompte.gestion_compte.dto;

import com.gestioncompte.gestion_compte.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {

    private final Long id;
    private final String accountNumber;
    private final BigDecimal balance;
    private final LocalDateTime createdAt;

    public AccountResponse(Long id, String accountNumber, BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}