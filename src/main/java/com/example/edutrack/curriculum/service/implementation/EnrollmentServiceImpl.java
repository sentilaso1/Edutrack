package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Enrollment;
import com.example.edutrack.curriculum.repository.EnrollmentRepository;
import com.example.edutrack.curriculum.service.interfaces.EnrollmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<CourseMentor> getPopularCoursesForGuest(int maxCount) {
        Pageable topPopular= PageRequest.of(0, maxCount);
        return enrollmentRepository.findPopularCoursesByEnrollmentCount(topPopular);
    }

    @Override
    public Enrollment getEnrollmentByCourseMentorId(UUID id) {
        return enrollmentRepository.findByCourseMentorId(id);
    }

    public int getLessonCountByCourseMentor(UUID courseMentorId) {
        Integer result = enrollmentRepository.totalSessionsByCourseMentorId(courseMentorId);
        return result != null ? result : 0;
    }

    public int getStudentCountByCourseMentor(UUID courseMentorId) {
        return enrollmentRepository.countByCourseMentor_IdAndIsApprovedTrue(courseMentorId);
    }

    @Override
    public CourseCardDTO mapToCourseCardDTO(CourseMentor courseMentor) {
        int lessonCount = getLessonCountByCourseMentor(courseMentor.getId());
        int studentCount = getStudentCountByCourseMentor(courseMentor.getId());

        return new CourseCardDTO(
                courseMentor.getId(),
                courseMentor.getCourse().getName(),
                lessonCount,
                studentCount,
                courseMentor.getMentor()
        );
    }

    @Override
    public List<CourseCardDTO> mapToCourseCardDTOList(List<CourseMentor> courseMentors) {
        return courseMentors.stream()
                .map(this::mapToCourseCardDTO)
                .toList();
    }

}
