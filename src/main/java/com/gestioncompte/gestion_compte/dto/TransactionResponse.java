package com.gestioncompte.gestion_compte.dto;

import com.gestioncompte.gestion_compte.model.Transaction;
import com.gestioncompte.gestion_compte.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private final Long id;
    private final String accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfterTransaction;
    private final String accountLinkedNumber;
    private final LocalDateTime transactionDate;

    public TransactionResponse(Long id, String accountNumber, TransactionType type, BigDecimal amount,
                                BigDecimal balanceAfterTransaction, String accountLinkedNumber,
                                LocalDateTime transactionDate) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.accountLinkedNumber = accountLinkedNumber;
        this.transactionDate = transactionDate;
    }

    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfterTransaction(),
                transaction.getAccountLinkedNumber(),
                transaction.getTransactionDate()
        );
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public String getAccountLinkedNumber() {
        return accountLinkedNumber;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}