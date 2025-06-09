package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.ApplicantsRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
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
    private final TagRepository tagRepository;
    private final MentorRepository mentorRepository;
    private final EnrollmentServiceImpl enrollmentServiceImpl;

    @Autowired
    public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository, ApplicantsRepository applicantsRepository, MenteeRepository menteeRepository, TagRepository tagRepository, MentorRepository mentorRepository, EnrollmentServiceImpl enrollmentServiceImpl) {
        this.courseMentorRepository = courseMentorRepository;
        this.applicantsRepository = applicantsRepository;
        this.menteeRepository = menteeRepository;
        this.tagRepository = tagRepository;
        this.mentorRepository = mentorRepository;
        this.enrollmentServiceImpl = enrollmentServiceImpl;
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

    @Override
    public List<CourseMentor> getRelatedCoursesByTags(UUID courseId, UUID menteeId, int limit) {
        List<Tag> tagListByCourseMentorId = tagRepository.findByCourseId(courseId);

        if (tagListByCourseMentorId.isEmpty()) {
            return Collections.emptyList();
        }

        tagListByCourseMentorId.forEach(tag -> System.out.println(" - " + tag.getTitle()));

        List<String> tagTitles = tagListByCourseMentorId.stream()
                .map(tag -> tag.getTitle().toLowerCase())
                .toList();

        System.out.println("[DEBUG] Tag titles for search: " + tagTitles);
        System.out.println("[DEBUG] Mentee ID to exclude enrolled courses: " + menteeId);

        Pageable pageable = PageRequest.of(0, limit);

        List<CourseMentor> result = courseMentorRepository.findRelatedByTagsAndNotEnrolled(tagTitles, menteeId, pageable);

        System.out.println("[DEBUG] Related course mentors found: " + result.size());
        result.forEach(cm -> System.out.println(" - Course: " + cm.getCourse().getName() + ", Mentor: " + cm.getMentor().getFullName()));
        if (result.size() < limit) {
            int remaining = Math.max(limit - result.size(), 0);
            List<CourseMentor> popularFallback = enrollmentServiceImpl.getPopularCoursesForGuest(remaining);

            Set<CourseMentor> combined = new LinkedHashSet<>(result);
            combined.addAll(popularFallback);
            return new ArrayList<>(combined).subList(0, Math.min(limit, combined.size()));
        }

        return result;
    }

    @Override
    public List<CourseMentor> getRecommendedByHistory(UUID menteeId, int limit) {
        List<String> tagTitlesByMenteeId = tagRepository.findDistinctTagTitlesFromMenteeId(menteeId);
        List<String> mentorExpertiseByMenteeId = mentorRepository.findExpertiseOfMentorsByMentee(menteeId);

        Set<String> keywords = new HashSet<>();
        keywords.addAll(tagTitlesByMenteeId);
        keywords.addAll(mentorExpertiseByMenteeId.stream().map(String::toLowerCase).toList());

        Pageable pageable = PageRequest.of(0, limit);
        List<CourseMentor> recommendations = courseMentorRepository.findRecommendedByKeywords(new ArrayList<>(keywords), menteeId, pageable);

        if (recommendations.size() < limit) {
            int remaining = Math.max(limit - recommendations.size(), 0);
            List<CourseMentor> fallback = enrollmentServiceImpl.getPopularCoursesForGuest(remaining);

            Set<CourseMentor> combined = new LinkedHashSet<>(recommendations);
            combined.addAll(fallback);
            return new ArrayList<>(combined).subList(0, Math.min(limit, combined.size()));
        }

        return recommendations;
    }
}


