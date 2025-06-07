package com.example.edutrack.transactions.repository;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUser(User user);
}
