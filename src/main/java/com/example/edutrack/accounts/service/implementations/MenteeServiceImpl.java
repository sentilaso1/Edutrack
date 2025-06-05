package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.example.edutrack.accounts.model.Mentee;

@Service
public class MenteeServiceImpl implements MenteeService {
    private final MenteeRepository menteeRepository;

    public MenteeServiceImpl(MenteeRepository menteeRepository) {
        this.menteeRepository = menteeRepository;
    }

    public Mentee getMenteeById(String id) {
        return menteeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Mentee not found"));
    }

    @Override
    public long countAll() {
        return menteeRepository.count();
    }

    @Override
    public List<Mentee> findAll() {
        return menteeRepository.findAll();
    }
}
