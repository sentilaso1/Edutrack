package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.ApplicantsRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import jakarta.validation.constraints.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseMentorServiceImpl implements CourseMentorService {
    private final CourseMentorRepository courseMentorRepository;
    private final ApplicantsRepository applicantsRepository;
    private final MenteeRepository menteeRepository;

    @Autowired
    public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository, ApplicantsRepository applicantsRepository, MenteeRepository menteeRepository) {
        this.courseMentorRepository = courseMentorRepository;
        this.applicantsRepository = applicantsRepository;
        this.menteeRepository = menteeRepository;
    }

    @Override
    public Page<CourseMentor> findAlByOrderByCreatedDateAsc(Pageable pageable) {
        return courseMentorRepository.findAlByOrderByCreatedDateAsc(pageable);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByCreatedDateDesc(Pageable pageable) {
        return courseMentorRepository.findAlByOrderByCreatedDateDesc(pageable);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByTitleDesc(Pageable pageable) {
        return courseMentorRepository.findAlByOrderByTitleDesc(pageable);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByTitleAsc(Pageable pageable) {
        return courseMentorRepository.findAlByOrderByTitleAsc(pageable);
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
        return courseMentorRepository.findFilteredCourseMentors(skillIds, subjectIds, pageable);
    }

    @Override
    public List<CourseMentor> findByCourseId(UUID courseMentorId) {
        return courseMentorRepository.findByCourseId(courseMentorId);
    }

    @Override
    public List<CourseMentor> findLatestCourse(int maxCount) {
        Pageable topLatest = PageRequest.of(0, maxCount);
        return courseMentorRepository.findLatestCourse(topLatest);
    }

    @Override
    public List<CourseMentor> getRecommendedCoursesByInterests(UUID menteeId, int limit) {
        Mentee foundMentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("Mentee not found"));
        List<String> interestKeywords = Arrays.stream(foundMentee.getInterests().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        Pageable pageable = PageRequest.of(0, limit);
        List<CourseMentor> matched = courseMentorRepository.findByTagsMatchingInterests(interestKeywords, pageable);
        if(matched.size() < limit){
            int remaining = Math.max(0, limit - matched.size());
            List<CourseMentor> fallback = courseMentorRepository.findLatestCourse(PageRequest.of(0, remaining));
            Set<CourseMentor> combined = new LinkedHashSet<>(matched);
            combined.addAll(fallback);
            return combined.stream().limit(limit).toList();
        }
        return courseMentorRepository.findByTagsMatchingInterests(interestKeywords, pageable);
    }
}
