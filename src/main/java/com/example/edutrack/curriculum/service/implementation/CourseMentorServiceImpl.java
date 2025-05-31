package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.ApplicantsRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseMentorServiceImpl implements CourseMentorService {
    private final CourseMentorRepository courseMentorRepository;
    private final ApplicantsRepository applicantsRepository;

    @Autowired
    public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository, ApplicantsRepository applicantsRepository) {
        this.courseMentorRepository = courseMentorRepository;
        this.applicantsRepository = applicantsRepository;
    }

    @Override
    public Page<CourseMentor> findAlByOrderByCreatedDateAsc(Pageable pageable) {
        return courseMentorRepository.findByStatusOrderByCreatedDateAsc(ApplicationStatus.ACCEPTED, pageable);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByCreatedDateDesc(Pageable pageable) {
        return courseMentorRepository.findByStatusOrderByCreatedDateDesc(ApplicationStatus.ACCEPTED, pageable);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByTitleDesc(Pageable pageable) {
        return courseMentorRepository.findByStatusOrderByTitleDesc(ApplicationStatus.ACCEPTED, pageable);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByTitleAsc(Pageable pageable) {
        return courseMentorRepository.findByStatusOrderByTitleAsc(ApplicationStatus.ACCEPTED, pageable);
    }

    public Page<CourseMentor> findAll(Pageable pageable) {
        return courseMentorRepository.findAll(pageable);
    }

    @Override
    public List<Tag> findAllTags() {
        return courseMentorRepository.findAllTags();
    }

    @Override
    public List<Course> findAllCourses() {
        return courseMentorRepository.findAllCourses();
    }

    @Override
    public CourseMentor findById(UUID id) {
        return applicantsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CourseMentor not found with id: " + id));
    }

    @Override
    public List<CourseMentor> getCourseMentorByMentorId(UUID id) {
        return applicantsRepository.findByMentorId(id);
    }


    @Override
    public Page<CourseMentor> findFilteredCourseMentors(List<UUID> skillIds, List<Integer> subjectIds, Pageable pageable) {
        return courseMentorRepository.findFilteredCourseMentors(ApplicationStatus.ACCEPTED, skillIds, subjectIds, pageable);
    }

    public List<CourseMentor> findByCourseId(UUID courseMentorId) {
        return courseMentorRepository.findByCourseId(courseMentorId);
    }

}
