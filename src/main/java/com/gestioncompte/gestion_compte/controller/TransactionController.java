package com.gestioncompte.gestion_compte.controller;

import com.gestioncompte.gestion_compte.dto.TransactionRequest;
import com.gestioncompte.gestion_compte.model.Transaction;
import com.gestioncompte.gestion_compte.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public Transaction deposit(@Valid @RequestBody TransactionRequest request) {
        return transactionService.deposit(request.getAccountNumber(), request.getAmount());
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@Valid @RequestBody TransactionRequest request) {
        return transactionService.withdraw(request.getAccountNumber(), request.getAmount());
    }

    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransactionRequest request) {
        transactionService.transfer(request.getAccountNumber(), request.getDestinationAccountNumber(), request.getAmount());
    }

    @GetMapping("/account/{accountId}")
    public Page<Transaction> getHistory(@PathVariable Long accountId, Pageable pageable) {
        return transactionService.getHistory(accountId, pageable);
    }
}