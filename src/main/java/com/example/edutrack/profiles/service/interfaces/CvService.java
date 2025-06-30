package com.example.edutrack.profiles.service.interfaces;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.profiles.dto.CVForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.edutrack.profiles.model.CV;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CvService {
    List<CV> findAllCVs();
    Page<CV> findAllCVsDateAsc(Pageable pageable);
    Page<CV> findAllCVsDateDesc(Pageable pageable);
    Page<CV> findAllCVsByStatusDateAsc(Pageable pageable, String status);
    Page<CV> findAllCVsByStatusDateDesc(Pageable pageable, String status);

    Page<CV> findAllCVsContainingSkills(List<String> skills, String sort, Pageable pageable);
    Page<CV> findAllCVsContainingSkillsByStatus(List<String> skills, String status, String sort, Pageable pageable);

    Page<CV> queryCVs(String filter, String sort, List<String> tags, Pageable pageable);
    List<String> getAllUniqueSkills();
    List<Course> getCoursesForCV(CV cv);

    void createCV(CVForm form, UUID mentorId);
    CV getCVById(UUID id);

    List<Course> getCoursesForCV(UUID cvId);
    boolean acceptCV(UUID id);
    boolean rejectCV(UUID id);

    void aiVerifyCV(CV cv);
    String generatePrompt(CV cv);
    void processAIResponse(CV cv, String aiJson);
    void scheduleAIVerification();
    boolean isBatchRunning();
    LocalDateTime getLastBatchEnd();
}
