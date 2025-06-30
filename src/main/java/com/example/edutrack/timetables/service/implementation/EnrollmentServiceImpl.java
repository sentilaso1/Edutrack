package com.example.edutrack.timetables.service.implementation;


import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return enrollmentRepository.findPopularCoursesByEnrollmentCount(topPopular, Enrollment.EnrollmentStatus.APPROVED);
    }

    public int getStudentCountByCourseMentor(UUID courseMentorId) {
        return enrollmentRepository.countByCourseMentor_IdAndStatus(courseMentorId, Enrollment.EnrollmentStatus.APPROVED);
    }

    @Override
    public CourseCardDTO mapToCourseCardDTO(CourseMentor courseMentor) {
        int studentCount = getStudentCountByCourseMentor(courseMentor.getId());
        return new CourseCardDTO(
                courseMentor.getId(),
                courseMentor.getCourse().getName(),
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

    @Override
    public List<Enrollment> getEnrollmentsByMenteeId(UUID menteeId) {
        return enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.CREATED);
    }

    @Override
    public List<CourseMentor> getCourseInProgressMentee(UUID menteeId) {
        return enrollmentRepository.findInProgressCourses(menteeId, Enrollment.EnrollmentStatus.APPROVED);
    }


    @Override
    public List<Enrollment> findByStatusAndMentor(Enrollment.EnrollmentStatus status, UUID mentorId) {
        return enrollmentRepository.findByStatus(status, mentorId);
    }

    @Override
    public Enrollment findById(Long id) {
        return enrollmentRepository.findById(id).get();
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> findOngoingEnrollments(UUID mentor) {
        LocalDate today = LocalDate.now();
        return enrollmentRepository.findOngoingEnrollments(today, mentor);
    }

    @Override
    public List<Enrollment> findAllApprovedEnrollments() {
        return enrollmentRepository.findAllApprovedEnrollments();
    }

    @Override
    public List<Course> findCourseByMenteeIdAndEnrollmentStatus(UUID menteeId){
        return enrollmentRepository.findByMenteeIdAndEnrollmentStatus(menteeId, Enrollment.EnrollmentStatus.APPROVED);
    }


    @Override
    public List<Mentor> findMentorsByMentee(UUID menteeId) {
        return enrollmentRepository.findDistinctMentorsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED);
    }

    @Override
    public List<CourseMentor> getCourseMentorsByMentee(UUID menteeId) {
        return enrollmentRepository.findCourseMentorByMentee(menteeId, Enrollment.EnrollmentStatus.APPROVED);
    }

}
