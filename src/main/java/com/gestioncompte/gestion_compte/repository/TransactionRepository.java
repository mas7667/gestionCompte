package com.gestioncompte.gestion_compte.repository;

import com.gestioncompte.gestion_compte.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId, Pageable pageable);
}