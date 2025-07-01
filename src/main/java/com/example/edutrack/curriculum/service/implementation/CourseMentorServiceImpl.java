package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.ApplicantsRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.service.implementation.EnrollmentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseMentorServiceImpl implements CourseMentorService {
    private final CourseMentorRepository courseMentorRepository;
    private final ApplicantsRepository applicantsRepository;
    private final MenteeRepository menteeRepository;
    private final TagRepository tagRepository;
    private final MentorRepository mentorRepository;
    private final EnrollmentServiceImpl enrollmentServiceImpl;
    private final EnrollmentRepository enrollmentRepository;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository,
                                   ApplicantsRepository applicantsRepository,
                                   MenteeRepository menteeRepository,
                                   TagRepository tagRepository,
                                   MentorRepository mentorRepository,
                                   EnrollmentServiceImpl enrollmentServiceImpl,
                                   EnrollmentRepository enrollmentRepository,
                                   FeedbackRepository feedbackRepository) {
        this.courseMentorRepository = courseMentorRepository;
        this.applicantsRepository = applicantsRepository;
        this.menteeRepository = menteeRepository;
        this.tagRepository = tagRepository;
        this.mentorRepository = mentorRepository;
        this.enrollmentServiceImpl = enrollmentServiceImpl;
        this.enrollmentRepository = enrollmentRepository;
        this.feedbackRepository = feedbackRepository;
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


    private List<CourseMentor> getRecommendationsByTags(
            Set<String> tagTitles, UUID menteeId, int limit
    ) {
        if (tagTitles.isEmpty()) return Collections.emptyList();

        List<CourseMentor> matches;

        if (menteeId == null) {
            matches = courseMentorRepository
                    .findRelatedByTags(new ArrayList<>(tagTitles), PageRequest.of(0, limit));
        } else {
            matches = courseMentorRepository
                    .findRelatedByTagsAndNotEnrolled(new ArrayList<>(tagTitles), menteeId, PageRequest.of(0, limit));
        }

        if (matches.size() < limit) {
            int remaining = limit - matches.size();
            List<CourseMentor> fallback = enrollmentServiceImpl.getPopularCoursesForGuest(remaining);

            Set<CourseMentor> combined = new LinkedHashSet<>(matches);
            combined.addAll(fallback);
            matches = new ArrayList<>(combined).subList(0, Math.min(limit, combined.size()));
        }

        return matches;
    }


    @Override
    public List<CourseMentor> getRelatedCoursesByTags(UUID courseId,UUID menteeId, int limit) {
        Set<String> tagTitles = tagRepository.findByCourseId(courseId).stream()
                .map(tag -> tag.getTitle().toLowerCase())
                .collect(Collectors.toSet());

        return getRecommendationsByTags(tagTitles, menteeId, limit);
    }

    @Override
    public List<CourseMentor> getRecommendedCourseMentors(UUID menteeId, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        List<Enrollment> enrollments = enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED);
        Set<String> tagTitles = new HashSet<>();
        for (Enrollment e : enrollments) {
            List<Tag> tags = tagRepository.findByCourseId(e.getCourseMentor().getCourse().getId());
            for (Tag tag : tags) {
                tagTitles.add(tag.getTitle().toLowerCase());
            }
        }
        return getRecommendationsByTags(tagTitles, menteeId, limit);
    }

    // Hàm F1
    @Override
    public List<CourseMentor> getRecommendedByHistory(UUID menteeId, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
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

    @Override
    public List<CourseMentor> getReviewablePairsForMentee(UUID menteeId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByMenteeId(menteeId);

        Set<CourseMentor> allPairs = enrollments.stream()
                .map(Enrollment::getCourseMentor)
                .collect(Collectors.toSet());

        return allPairs.stream()
                .filter(cm -> feedbackRepository.findByMenteeIdAndCourseMentorId(menteeId, cm.getId()).isEmpty())
                .collect(Collectors.toList());
    }
}


