package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.CourseMentor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorService {
    public Mentor getMentorById(String id);

    Optional<Mentor> findById(UUID id);

    long countAll();

    List<Mentor> findAll();

    List<Mentor> getTopMentorsByRatingOrSessions(int limit);

    List<Mentor> findMentorsByMenteeInterest(UUID menteeId, int limit);

    Optional<Mentor> promoteToMentor(UUID userId);
    List<CourseMentor> getCourseMentorRelations(UUID mentorId);
}
