package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorService {
        public Mentor getMentorById(String id);

    Optional<Mentor> findById(UUID id);

}
