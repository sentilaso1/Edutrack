package com.example.edutrack.transactions.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.repository.WalletRepository;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Optional<Wallet> findByUser(User user) {
        return walletRepository.findByUser(user);
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return walletRepository.findById(id);
    }

    @Override
    public Wallet save(User user) {
        return walletRepository.save(new Wallet(user));
    }
}
