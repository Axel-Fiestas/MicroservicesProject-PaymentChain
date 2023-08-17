package com.paymentchain.businessdomain.transaction.repository;

import com.paymentchain.businessdomain.transaction.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public List<Transaction> findByAccountIban(String accountIban);
}
