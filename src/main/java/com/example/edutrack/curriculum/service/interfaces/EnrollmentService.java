package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Enrollment;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    public List<CourseMentor> getPopularCoursesForGuest(int topPopular);

    Enrollment getEnrollmentByCourseMentorId(UUID id);

    CourseCardDTO mapToCourseCardDTO(CourseMentor courseMentor);

    List<CourseCardDTO> mapToCourseCardDTOList(List<CourseMentor> courseMentors);
}
