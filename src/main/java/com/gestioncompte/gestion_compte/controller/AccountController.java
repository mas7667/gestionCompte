package com.gestioncompte.gestion_compte.controller;

import com.gestioncompte.gestion_compte.dto.AccountResponse;
import com.gestioncompte.gestion_compte.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        return AccountResponse.fromEntity(accountService.getByAccountNumber(accountNumber));
    }

    @GetMapping("/client/{clientId}")
    public List<AccountResponse> listAccountsForClient(@PathVariable Long clientId) {
        return accountService.listAccountsForClient(clientId).stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }
}