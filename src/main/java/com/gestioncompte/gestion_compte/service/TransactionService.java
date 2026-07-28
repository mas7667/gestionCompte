package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.model.Account;
import com.gestioncompte.gestion_compte.model.Transaction;
import com.gestioncompte.gestion_compte.model.TransactionType;
import com.gestioncompte.gestion_compte.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = accountService.getByAccountNumber(accountNumber);

        account.setBalance(account.getBalance().add(amount));
        accountService.save(account);

        Transaction transaction = new Transaction(account, TransactionType.DEPOSIT, amount, account.getBalance());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = accountService.getByAccountNumber(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException(
                    "Insufficient balance. Current balance: " + account.getBalance() + ", requested: " + amount);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountService.save(account);

        Transaction transaction = new Transaction(account, TransactionType.WITHDRAWAL, amount, account.getBalance());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        validatePositiveAmount(amount);

        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("Source and destination accounts must be different.");
        }

        Account source = accountService.getByAccountNumber(sourceAccountNumber);
        Account destination = accountService.getByAccountNumber(destinationAccountNumber);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException(
                    "Insufficient balance for transfer. Current balance: " + source.getBalance());
        }

        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));
        accountService.save(source);
        accountService.save(destination);

        Transaction outgoing = new Transaction(source, TransactionType.TRANSFER_OUT, amount, source.getBalance());
        outgoing.setAccountLinkedNumber(destination.getAccountNumber());
        transactionRepository.save(outgoing);

        Transaction incoming = new Transaction(destination, TransactionType.TRANSFER_IN, amount, destination.getBalance());
        incoming.setAccountLinkedNumber(source.getAccountNumber());
        transactionRepository.save(incoming);
    }

    public Page<Transaction> getHistory(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId, pageable);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}