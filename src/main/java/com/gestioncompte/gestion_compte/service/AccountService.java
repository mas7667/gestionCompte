package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.model.Account;
import com.gestioncompte.gestion_compte.model.Client;
import com.gestioncompte.gestion_compte.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Client client) {
        Account account = new Account(client);
        return accountRepository.save(account);
    }

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException(
                        "No account found with number: " + accountNumber));
    }

    public List<Account> listAccountsForClient(Long clientId) {
        return accountRepository.findByClientId(clientId);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}