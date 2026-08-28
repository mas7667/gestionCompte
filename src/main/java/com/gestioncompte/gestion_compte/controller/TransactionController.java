package com.gestioncompte.gestion_compte.controller;

import com.gestioncompte.gestion_compte.dto.TransactionRequest;
import com.gestioncompte.gestion_compte.dto.TransactionResponse;
import com.gestioncompte.gestion_compte.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public TransactionResponse deposit(@Valid @RequestBody TransactionRequest request, Authentication authentication) {
        return TransactionResponse.fromEntity(
                transactionService.depositFor(authentication.getName(), request.getAccountNumber(), request.getAmount()));
    }

    @PostMapping("/withdraw")
    public TransactionResponse withdraw(@Valid @RequestBody TransactionRequest request, Authentication authentication) {
        return TransactionResponse.fromEntity(
                transactionService.withdrawFor(authentication.getName(), request.getAccountNumber(), request.getAmount()));
    }

    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransactionRequest request, Authentication authentication) {
        transactionService.transferFor(authentication.getName(), request.getAccountNumber(), request.getDestinationAccountNumber(), request.getAmount());
    }

    @GetMapping("/account/{accountId}")
    public Page<TransactionResponse> getHistory(@PathVariable Long accountId, Pageable pageable, Authentication authentication) {
        return transactionService.getHistoryFor(authentication.getName(), accountId, pageable).map(TransactionResponse::fromEntity);
    }
}
