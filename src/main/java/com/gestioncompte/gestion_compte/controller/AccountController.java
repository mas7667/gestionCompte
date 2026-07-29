package com.gestioncompte.gestion_compte.controller;

import com.gestioncompte.gestion_compte.model.Account;
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
    public Account getAccount(@PathVariable String accountNumber) {
        return accountService.getByAccountNumber(accountNumber);
    }

    @GetMapping("/client/{clientId}")
    public List<Account> listAccountsForClient(@PathVariable Long clientId) {
        return accountService.listAccountsForClient(clientId);
    }
}