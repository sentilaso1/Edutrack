package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Mentor;

import java.util.Optional;
import java.util.UUID;

public interface MentorService {
    public Mentor getMentorById(String id);

    Optional<Mentor> findById(UUID id);

    long countAll();
}
