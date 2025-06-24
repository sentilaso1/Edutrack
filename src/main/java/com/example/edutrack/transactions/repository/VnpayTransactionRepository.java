package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.VnpayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VnpayTransactionRepository extends JpaRepository<VnpayTransaction, UUID> {
    Optional<VnpayTransaction> findByTxnRef(String txnRef);
}
