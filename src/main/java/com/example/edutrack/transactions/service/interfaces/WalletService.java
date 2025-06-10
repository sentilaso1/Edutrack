package com.example.edutrack.transactions.service.interfaces;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletService {
    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findById(UUID id);

    Wallet save(User user);
    Optional<Wallet> addFunds(User user, Double amount);
}
