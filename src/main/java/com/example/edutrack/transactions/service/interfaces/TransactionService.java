package com.example.edutrack.transactions.service.interfaces;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {
    List<Transaction> findAll();

    List<Transaction> findByWallet(Wallet wallet);

    List<Transaction> findByUser(User user);
    List<Transaction> findByUser(UUID userId);

    Optional<Transaction> findById(UUID id);
    Transaction save(Transaction transaction);
}
