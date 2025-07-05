package com.example.edutrack.timetables.service.implementation;


import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentScheduleService enrollmentScheduleService;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, EnrollmentScheduleService enrollmentScheduleService) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentScheduleService = enrollmentScheduleService;
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

    @Override
    public int getNumberOfPendingSlot(Mentor mentor, LocalDate date, Slot slot){
        List<Enrollment> enrollment = enrollmentRepository.findByStatus(Enrollment.EnrollmentStatus.PENDING,mentor.getId());
        List<String> summaries = enrollment.stream().map(Enrollment::getScheduleSummary).toList();
        int count = 0;
        for(String summary : summaries){
            List<RequestedSchedule> requestedScheduleList = enrollmentScheduleService.findStartLearningTime(summary);
            for(RequestedSchedule requestedSchedule : requestedScheduleList){
                if(requestedSchedule.getSlot().equals(slot) && requestedSchedule.getRequestedDate().equals(date)){
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public List<Enrollment> getDuplicatedSchedules(Enrollment enrollment) {
        List<Enrollment> pendingEnrollments = enrollmentRepository.findByStatus(Enrollment.EnrollmentStatus.PENDING, enrollment.getCourseMentor().getMentor().getId());

        String[] schedules = enrollment.getScheduleSummary().split(";");
        Iterator<Enrollment> iterator = pendingEnrollments.iterator();

        while (iterator.hasNext()) {
            Enrollment pendingEnrollment = iterator.next();
            if (!isContainSchedule(pendingEnrollment, schedules) || (long) pendingEnrollment.getId() == enrollment.getId()) {
                iterator.remove();
            }
        }

        return pendingEnrollments;
    }


    private boolean isContainSchedule(Enrollment pendingEnrollment, String[] schedule) {
        String summary = pendingEnrollment.getScheduleSummary();
        for(String s : schedule){
            if(summary.contains(s.trim())){
                return true;
            }
        }
        return false;
    }

}
