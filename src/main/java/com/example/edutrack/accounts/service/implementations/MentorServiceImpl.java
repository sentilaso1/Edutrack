package com.example.edutrack.accounts.service.implementations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import org.springframework.stereotype.Service;

import com.example.edutrack.accounts.model.Mentor;

@Service
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;

    public MentorServiceImpl(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    public Mentor getMentorById(String id) {
        try {
            return mentorRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mentor not found with id: " + id));
        } catch (IllegalArgumentException e) {
            return null;
        }

    }


    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }


    public Optional<Mentor> findById(UUID id) {
        return mentorRepository.findById(id);
    }

    @Override
    public long countAll() {
        return mentorRepository.count();
    }

    @Override
    public List<Mentor> findAll() {
        return mentorRepository.findAll();
    }
}
