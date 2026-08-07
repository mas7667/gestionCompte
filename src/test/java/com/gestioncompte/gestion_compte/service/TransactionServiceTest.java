package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.model.Account;
import com.gestioncompte.gestion_compte.model.Transaction;
import com.gestioncompte.gestion_compte.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountService accountService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountService = mock(AccountService.class);
        transactionService = new TransactionService(transactionRepository, accountService);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void depositIncreasesBalanceAndSavesTransaction() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));
        when(accountService.getByAccountNumber("ACC1")).thenReturn(account);

        Transaction result = transactionService.deposit("ACC1", new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), account.getBalance());
        assertEquals(new BigDecimal("150.00"), result.getBalanceAfterTransaction());
        verify(accountService).save(account);
    }

    @Test
    void depositRejectsZeroAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit("ACC1", BigDecimal.ZERO));
        verifyNoInteractions(accountService);
    }

    @Test
    void depositRejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit("ACC1", new BigDecimal("-10.00")));
        verifyNoInteractions(accountService);
    }

    @Test
    void withdrawDecreasesBalanceAndSavesTransaction() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));
        when(accountService.getByAccountNumber("ACC1")).thenReturn(account);

        Transaction result = transactionService.withdraw("ACC1", new BigDecimal("40.00"));

        assertEquals(new BigDecimal("60.00"), account.getBalance());
        assertEquals(new BigDecimal("60.00"), result.getBalanceAfterTransaction());
        verify(accountService).save(account);
    }

    @Test
    void withdrawRejectsInsufficientBalance() {
        Account account = new Account();
        account.setBalance(new BigDecimal("10.00"));
        when(accountService.getByAccountNumber("ACC1")).thenReturn(account);

        assertThrows(IllegalStateException.class,
                () -> transactionService.withdraw("ACC1", new BigDecimal("50.00")));
        verify(accountService, never()).save(any());
    }

    @Test
    void withdrawRejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.withdraw("ACC1", new BigDecimal("-5.00")));
        verifyNoInteractions(accountService);
    }

    @Test
    void transferMovesBalanceBetweenAccounts() {
        Account source = new Account();
        source.setBalance(new BigDecimal("100.00"));
        Account destination = new Account();
        destination.setBalance(new BigDecimal("20.00"));
        when(accountService.getByAccountNumber("SRC")).thenReturn(source);
        when(accountService.getByAccountNumber("DST")).thenReturn(destination);

        transactionService.transfer("SRC", "DST", new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), source.getBalance());
        assertEquals(new BigDecimal("50.00"), destination.getBalance());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferRejectsInsufficientBalance() {
        Account source = new Account();
        source.setBalance(new BigDecimal("10.00"));
        Account destination = new Account();
        destination.setBalance(BigDecimal.ZERO);
        when(accountService.getByAccountNumber("SRC")).thenReturn(source);
        when(accountService.getByAccountNumber("DST")).thenReturn(destination);

        assertThrows(IllegalStateException.class,
                () -> transactionService.transfer("SRC", "DST", new BigDecimal("50.00")));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferRejectsSameSourceAndDestination() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer("ACC1", "ACC1", new BigDecimal("10.00")));
        verifyNoInteractions(accountService);
    }

    @Test
    void transferRejectsZeroAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer("SRC", "DST", BigDecimal.ZERO));
        verifyNoInteractions(accountService);
    }
}