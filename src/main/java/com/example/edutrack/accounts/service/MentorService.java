package com.example.edutrack.accounts.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;

    public MentorService(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    // Receiving all mentors
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    // Filter mentors
    public List<Mentor> searchMentors(String name, String expertise, Double rating, Integer totalSessions, Boolean isAvailable) {
        return mentorRepository.searchMentors(name, expertise, rating, totalSessions, isAvailable);
    }
}
