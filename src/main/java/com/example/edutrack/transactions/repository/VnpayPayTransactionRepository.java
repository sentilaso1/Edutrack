package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.VnpayPayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VnpayPayTransactionRepository extends JpaRepository<VnpayPayTransaction, Long> {
    Optional<VnpayPayTransaction> findByTxnRef(String txnRef);
}
