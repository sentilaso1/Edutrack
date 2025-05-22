package com.example.edutrack.auth.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }
}