package com.example.edutrack.transactions.repository;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.BankingQR;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankingQrRepository extends JpaRepository<BankingQR, UUID> {
    Optional<BankingQR> findByUser(User user);
}
