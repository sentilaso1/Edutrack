package com.example.edutrack.transactions.service.interfaces;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> findByWallet(Wallet wallet) {
        return transactionRepository.findByWallet(wallet);
    }

    @Override
    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByUserId(user.getId());
    }

    @Override
    public List<Transaction> findByUser(UUID userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
