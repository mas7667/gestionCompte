package com.gestioncompte.gestion_compte.controller;

import com.gestioncompte.gestion_compte.dto.AccountResponse;
import com.gestioncompte.gestion_compte.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber, Authentication authentication) {
        return AccountResponse.fromEntity(accountService.getOwnedAccount(accountNumber, authentication.getName()));
    }

    @GetMapping("/me")
    public List<AccountResponse> listMyAccounts(Authentication authentication) {
        return accountService.listAccountsForEmail(authentication.getName()).stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }
}
