package com.paymentchain.businessdomain.transaction.repository;

import com.paymentchain.businessdomain.transaction.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
