package com.example.edutrack.transactions.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.BankingQR;
import com.example.edutrack.transactions.repository.BankingQrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankingQrService {
    private final BankingQrRepository qrRepository;

    @Autowired
    public BankingQrService(BankingQrRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    public BankingQR save(BankingQR qr) {
        return qrRepository.save(qr);
    }

    public Optional<BankingQR> findByUser(User user) {
        return qrRepository.findByUser(user);
    }
}
