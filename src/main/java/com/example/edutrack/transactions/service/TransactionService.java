package com.example.edutrack.transactions.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> findByWallet(Wallet wallet) {
        return transactionRepository.findByWallet(wallet);
    }

    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByUserId(user.getId());
    }

    public List<Transaction> findByUser(UUID userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
