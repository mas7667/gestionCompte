package com.gestioncompte.gestion_compte.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;

    private String accountLinkedNumber;

    private LocalDateTime transactionDate = LocalDateTime.now();


    public Transaction(Account account, com.gestioncompte.gestion_compte.model.TransactionType withdrawal, BigDecimal amount, BigDecimal balance){

    }

    public Transaction(Account account, TransactionType type, BigDecimal amount, BigDecimal balanceAfterTransaction){
        this.account = account;
        this.type =  type;
        this.amount  = amount;
        this.balanceAfterTransaction =  balanceAfterTransaction;
    }

    public Long getId(){
        return id;
    }

    public Account getAccount(){
        return account;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public BigDecimal getBalanceAfterTransaction(){
        return balanceAfterTransaction;
    }

    public String getAccountLinkedNumber(){
        return accountLinkedNumber;
    }

    public LocalDateTime getTransactionDate(){
        return transactionDate;
    }


    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFERT_OUT,
        TRANSFERT_IN
    }
}
