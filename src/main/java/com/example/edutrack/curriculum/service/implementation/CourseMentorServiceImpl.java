package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.curriculum.model.*;
import com.example.edutrack.curriculum.repository.*;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.DashboardService;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import com.example.edutrack.timetables.service.implementation.EnrollmentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final DashboardService dashboardService;
    private final CVCourseRepository cvCourseRepository;
    private final EnrollmentScheduleRepository enrollmentScheduleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository, ApplicantsRepository applicantsRepository, MenteeRepository menteeRepository, TagRepository tagRepository, MentorRepository mentorRepository, EnrollmentServiceImpl enrollmentServiceImpl, EnrollmentRepository enrollmentRepository, FeedbackRepository feedbackRepository, DashboardService dashboardService, CVCourseRepository cvCourseRepository, EnrollmentScheduleRepository enrollmentScheduleRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.courseMentorRepository = courseMentorRepository;
        this.applicantsRepository = applicantsRepository;
        this.menteeRepository = menteeRepository;
        this.tagRepository = tagRepository;
        this.mentorRepository = mentorRepository;
        this.enrollmentServiceImpl = enrollmentServiceImpl;
        this.enrollmentRepository = enrollmentRepository;
        this.feedbackRepository = feedbackRepository;
        this.dashboardService = dashboardService;
        this.cvCourseRepository = cvCourseRepository;
        this.enrollmentScheduleRepository = enrollmentScheduleRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }


    @Override
    public Page<CourseMentor> findAlByOrderByCreatedDateAsc(Pageable pageable, String search) {

        return courseMentorRepository.findAlByOrderByCreatedDateAsc(pageable, search);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByCreatedDateDesc(Pageable pageable, String search) {

        return courseMentorRepository.findAlByOrderByCreatedDateDesc(pageable, search);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByTitleDesc(Pageable pageable, String search) {

        return courseMentorRepository.findAlByOrderByTitleDesc(pageable, search);
    }

    @Override
    public Page<CourseMentor> findAlByOrderByTitleAsc(Pageable pageable, String search) {

        return courseMentorRepository.findAlByOrderByTitleAsc(pageable, search);
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
    public Page<CourseMentor> findFilteredCourseMentors(List<UUID> skillIds, List<Integer> subjectIds, Pageable pageable, String search) {

        return courseMentorRepository.findFilteredCourseMentors(skillIds, subjectIds, pageable, search);
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
                .filter(courseMentor ->
                        dashboardService.hasCompletedCourse(courseMentor, menteeRepository.findById(menteeId).orElseThrow()).orElse(false))
                .collect(Collectors.toSet());

        return allPairs.stream()
                .filter(cm -> feedbackRepository.findByMenteeIdAndCourseMentorId(menteeId, cm.getId()).isEmpty())
                .collect(Collectors.toList());
    }


    public void updatePrices(UUID mentorId, List<UUID> courseIds, List<Double> prices) {
        for (int i = 0; i < courseIds.size(); i++) {
            UUID courseId = courseIds.get(i);
            Double price = prices.get(i);
            if (price != null) {
                Optional<CourseMentor> opt = courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId);
                opt.ifPresent(cm -> {
                    cm.setPrice(price);
                    courseMentorRepository.save(cm);
                });
            }
        }
    }

    @Override
    public List<CourseMentor> findByMentorId(UUID mentorId) {
        return courseMentorRepository.findByMentorId(mentorId);
    }

    @Override
    public Page<CourseMentor> findByMentorIdPaged(UUID mentorId, Pageable pageable) {
        return courseMentorRepository.findByMentorId(mentorId, pageable);
    }

    @Override
    public boolean isCourseLocked(UUID courseId) {
        boolean hasNonRejectedCVs = cvCourseRepository.existsByCourse_IdAndCv_StatusNot(courseId, CV.STATUS_REJECTED);
        if (hasNonRejectedCVs) {
            return true;
        }
        boolean hasActiveOrFutureSessions = enrollmentScheduleRepository
                .existsByEnrollment_CourseMentor_Course_IdAndDateAfter(courseId, LocalDate.now().minusDays(1));
        if (hasActiveOrFutureSessions) {
            return true;
        }
        return false;
    }


    public void addCourseMentor(UUID userId, UUID courseId, String description) {
        Mentor mentor = mentorRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (courseMentorRepository.existsByMentorIdAndCourseId(userId, courseId)) {
            return;
        }

        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setMentor(mentor);
        courseMentor.setCourse(course);
        courseMentor.setDescription(description);
        courseMentorRepository.save(courseMentor);
    }

    public void removeCourseMentor(UUID userId, UUID courseId) {
        CourseMentor courseMentor = courseMentorRepository.findByMentorIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("CourseMentor not found: userId=" + userId + ", courseId=" + courseId));
        courseMentorRepository.delete(courseMentor);
    }
    public void updateCourseMentorDescription(UUID userId, UUID courseId, String description) {
        CourseMentor courseMentor = courseMentorRepository.findByMentorIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("CourseMentor not found: userId=" + userId + ", courseId=" + courseId));
        courseMentor.setDescription(description);
        courseMentorRepository.save(courseMentor);
    }

    @Override
    public long countCoursesByMentor(Mentor mentor) {
        return courseMentorRepository.countByMentorAndStatus(mentor, ApplicationStatus.ACCEPTED);
    }

    @Override
    public boolean alreadyHasPendingEnrollment(CourseMentor courseMentor, Mentee mentee) {
        return courseMentorRepository.alreadyHasPendingEnrollment(courseMentor, mentee);
    }

    @Override
    public void save(CourseMentor cm) {
        courseMentorRepository.save(cm);
    }
}


