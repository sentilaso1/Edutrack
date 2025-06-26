package com.example.edutrack.transactions.service;

import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.model.Withdrawal;
import com.example.edutrack.transactions.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;

    @Autowired
    public WithdrawalService(WithdrawalRepository withdrawalRepository) {
        this.withdrawalRepository = withdrawalRepository;
    }

    public Withdrawal save(Withdrawal withdrawal) {
        return withdrawalRepository.save(withdrawal);
    }

    public List<Withdrawal> findByWallet(Wallet wallet) {
        return withdrawalRepository.findByWallet(wallet);
    }

    public Page<Withdrawal> findAll(Pageable pageable) {
        return withdrawalRepository.findAll(pageable);
    }

    public Page<Withdrawal> findByStatus(Withdrawal.Status status, Pageable pageable) {
        return withdrawalRepository.findByStatus(status, pageable);
    }

    public Page<Withdrawal> findByAmountRange(Double minAmount, Double maxAmount, Pageable pageable) {
        return withdrawalRepository.findByAmountBetween(minAmount, maxAmount, pageable);
    }

    public Page<Withdrawal> findByStatusAndAmountRange(Withdrawal.Status status, Double minAmount, Double maxAmount, Pageable pageable) {
        return withdrawalRepository.findByStatusAndAmountBetween(status, minAmount, maxAmount, pageable);
    }

    public Page<Withdrawal> findByWallet(Wallet wallet, Pageable pageable) {
        return withdrawalRepository.findByWallet(wallet, pageable);
    }

    public Page<Withdrawal> findByWalletAndStatus(Wallet wallet, Withdrawal.Status status, Pageable pageable) {
        return withdrawalRepository.findByWalletAndStatus(wallet, status, pageable);
    }

    public Page<Withdrawal> findByWalletAndAmountRange(Wallet wallet, Double minAmount, Double maxAmount, Pageable pageable) {
        return withdrawalRepository.findByWalletAndAmountBetween(wallet, minAmount, maxAmount, pageable);
    }

    public Page<Withdrawal> findByWalletAndStatusAndAmountRange(Wallet wallet, Withdrawal.Status status, Double minAmount, Double maxAmount, Pageable pageable) {
        return withdrawalRepository.findByWalletAndStatusAndAmountBetween(wallet, status, minAmount, maxAmount, pageable);
    }

    public Double getMinAmountByWallet(Wallet wallet) {
        return withdrawalRepository.findMinAmountByWallet(wallet);
    }

    public Double getMaxAmountByWallet(Wallet wallet) {
        return withdrawalRepository.findMaxAmountByWallet(wallet);
    }
}
