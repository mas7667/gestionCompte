package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.model.Account;
import com.gestioncompte.gestion_compte.model.Client;
import com.gestioncompte.gestion_compte.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.access.AccessDeniedException;

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

    public Account getOwnedAccount(String accountNumber, String email) {
        Account account = getByAccountNumber(accountNumber);
        assertOwnedBy(account, email);
        return account;
    }

    public Account getOwnedAccount(Long accountId, String email) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("No account found with id: " + accountId));
        assertOwnedBy(account, email);
        return account;
    }

    public List<Account> listAccountsForEmail(String email) {
        return accountRepository.findByClientEmail(email);
    }

    private void assertOwnedBy(Account account, String email) {
        if (account.getClient() == null || !account.getClient().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("You do not have access to this account.");
        }
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
