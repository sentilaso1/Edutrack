package com.example.edutrack.auth.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        User savedUser = userRepository.save(user);
        walletService.save(savedUser);
        return savedUser;
    }

    public Optional<User> findByResetToken(String token){
        return userRepository.findByResetToken(token);
    }

}