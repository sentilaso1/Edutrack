package com.example.edutrack.timetables.service.interfaces;

import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Enrollment;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    List<CourseMentor> getPopularCoursesForGuest(int topPopular);

    CourseCardDTO mapToCourseCardDTO(CourseMentor courseMentor);

    List<CourseCardDTO> mapToCourseCardDTOList(List<CourseMentor> courseMentors);

    List<Enrollment> getEnrollmentsByMenteeId(UUID menteeId);

    List<CourseMentor> getCourseInProgressMentee(UUID menteeId);

    List<Enrollment> findByStatusAndMentor(Enrollment.EnrollmentStatus status, UUID mentorId);

    Enrollment findById(Long id);

    List<Course> findCourseByMenteeIdAndEnrollmentStatus(UUID menteeId);

    Enrollment save(Enrollment enrollment);

    List<Enrollment> findOngoingEnrollments(UUID mentor);
}
