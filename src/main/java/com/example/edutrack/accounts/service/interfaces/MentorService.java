package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.dto.IncomeStatsDTO;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<Mentor> searchMentorsWithApprovedCV(String name, String[] expertise, Double rating, Integer totalSessions, Boolean isAvailable, Pageable pageable);
    List<String> getAllMentorExpertiseFromApprovedCVs();
    Optional<Mentor> getMentorById(UUID id);
    List<Course> getCoursesByMentor(UUID id);
    List<Tag> getTagsByMentor(UUID id);
    Optional<CV> getCVById(UUID id);
    IncomeStatsDTO getIncomeStats(UUID mentorId);
    List<String> getAllMentorSkills();
    Optional<Mentor> findByEmail(String email);
    void save(Mentor mentor);
}
