package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.CourseMentor;

import java.util.List;
import java.util.UUID;

public interface CourseMentorService {
    List<CourseMentor> findByCourseId(UUID courseId);
    void deleteById(UUID applicantId);
}
