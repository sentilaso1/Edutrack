package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByWallet(Wallet wallet);

    @Query("SELECT t FROM Transaction t WHERE t.wallet.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") UUID userId);
}
