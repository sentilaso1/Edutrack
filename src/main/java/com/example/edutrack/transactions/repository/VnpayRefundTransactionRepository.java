package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.VnpayRefundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VnpayRefundTransactionRepository extends JpaRepository<VnpayRefundTransaction, Long> {
    Optional<VnpayRefundTransaction> findByTxnRef(String txnRef);
}
