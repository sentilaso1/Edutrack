package com.example.edutrack.timetables.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
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

    List<Enrollment> findOngoingEnrollments(Mentor mentor);

    List<Enrollment> findCompletedEnrollments(Mentor mentor);

    List<Course> findCourseByMenteeIdAndEnrollmentStatus(UUID menteeId);

    Enrollment save(Enrollment enrollment);

    List<Mentor> findMentorsByMentee(UUID menteeId);

    List<Enrollment> findAllApprovedEnrollments();

    List<CourseMentor> getCourseMentorsByMentee(UUID menteeId);

    List<Enrollment> findPendingEnrollmentsForMentee(UUID menteeId);

    int getNumberOfPendingSlot(Mentor mentor, LocalDate date, Slot slot);

    List<Enrollment> getDuplicatedSchedules(Enrollment enrollment);

    Double getPercentComplete(Enrollment enrollment);

    Page<Enrollment> findAll(Specification<Enrollment> spec, Pageable pageable);

    Page<Enrollment> findEnrollmentsWithFilters(Mentor mentor, String status, String courseName, String menteeName, Pageable pageable);

    List<Mentor> findAllUniqueMentors();

    List<Mentee> findAllUniqueMentees();
}
