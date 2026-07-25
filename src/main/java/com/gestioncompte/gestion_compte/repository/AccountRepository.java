package com.gestioncompte.gestion_compte.repository;

import com.gestioncompte.gestion_compte.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByClientId(Long clientId);
}