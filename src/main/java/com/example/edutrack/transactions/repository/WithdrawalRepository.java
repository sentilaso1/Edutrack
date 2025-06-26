package com.example.edutrack.transactions.repository;

import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.model.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, UUID> {
    List<Withdrawal> findByWallet(Wallet wallet);

    Page<Withdrawal> findByWallet(Wallet wallet, Pageable pageable);
    Page<Withdrawal> findByWalletAndStatus(Wallet wallet, Withdrawal.Status status, Pageable pageable);
    Page<Withdrawal> findByWalletAndAmountBetween(Wallet wallet, Double minAmount, Double maxAmount, Pageable pageable);
    Page<Withdrawal> findByWalletAndStatusAndAmountBetween(Wallet wallet, Withdrawal.Status status, Double minAmount, Double maxAmount, Pageable pageable);

    Page<Withdrawal> findByStatus(Withdrawal.Status status, Pageable pageable);
    Page<Withdrawal> findByAmountBetween(Double minAmount, Double maxAmount, Pageable pageable);
    Page<Withdrawal> findByStatusAndAmountBetween(Withdrawal.Status status, Double minAmount, Double maxAmount, Pageable pageable);

    @Query("SELECT MIN(w.amount) FROM Withdrawal w WHERE w.wallet = :wallet")
    Double findMinAmountByWallet(@Param("wallet") Wallet wallet);

    @Query("SELECT MAX(w.amount) FROM Withdrawal w WHERE w.wallet = :wallet")
    Double findMaxAmountByWallet(@Param("wallet") Wallet wallet);
}
