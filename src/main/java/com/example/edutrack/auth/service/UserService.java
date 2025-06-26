package com.example.edutrack.auth.service;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.transactions.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;
    @Autowired
    private MenteeRepository menteeRepository;
    @Autowired
    private MentorRepository mentorRepository;

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

    public void registerMentee(User user) {
        Mentee mentee = new Mentee();
        mentee.setEmail(user.getEmail());
        mentee.setPassword(user.getPassword());
        mentee.setFullName(user.getFullName());
        mentee.setPhone(user.getPhone());
        mentee.setGender(user.getGender());
        mentee.setIsActive(true);
        mentee.setIsLocked(false);
        mentee.setTotalSessions(0);
        mentee.setInterests("");
        menteeRepository.save(mentee);
    }

    public void registerMentor(User user) {
        Mentor mentor = new Mentor();
        mentor.setEmail(user.getEmail());
        mentor.setPassword(user.getPassword());
        mentor.setFullName(user.getFullName());
        mentor.setPhone(user.getPhone());
        mentor.setGender(user.getGender());
        mentor.setIsActive(true);
        mentor.setIsLocked(false);
        mentor.setAvailable(true);
        mentor.setTotalSessions(0);
        mentor.setExpertise("");
        mentor.setRating(0.0);
        mentorRepository.save(mentor);
    }

    public boolean isMentor(User user){
        Optional<Mentor> mentor = mentorRepository.findById(user.getId());
        return mentor.isPresent();
    }

    public boolean isMentee(User user){
        Optional<Mentee> mentee = menteeRepository.findById(user.getId());
        return mentee.isPresent();
    }
}