package com.example.edutrack.transactions.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Optional<Wallet> findByUser(User user) {
        return walletRepository.findByUser(user);
    }

    public Optional<Wallet> findById(UUID id) {
        return walletRepository.findById(id);
    }

    public Wallet save(User user) {
        return walletRepository.save(new Wallet(user));
    }

    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Optional<Wallet> addFunds(User user, Double amount) {
        Optional<Wallet> walletOpt = walletRepository.findByUser(user);
        if (walletOpt.isEmpty()) {
            return Optional.empty();
        }

        Wallet wallet = walletOpt.get();
        wallet.setBalance(wallet.getBalance() + amount);
        return Optional.of(walletRepository.save(wallet));
    }
}
