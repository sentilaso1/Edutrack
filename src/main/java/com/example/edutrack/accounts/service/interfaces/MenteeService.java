package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenteeService {
    Mentee getMenteeById(String id);

    long countAll();

    List<Mentee> findAll();

    Optional<Mentee> findById(UUID id);
}
