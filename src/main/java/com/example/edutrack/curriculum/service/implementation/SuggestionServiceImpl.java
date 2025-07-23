package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.curriculum.dto.TagEnrollmentCountDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.SuggestionType;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import com.example.edutrack.curriculum.service.interfaces.SuggestionService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    private final CourseMentorService courseMentorService;
    private final EnrollmentService enrollmentService;
    private final MentorService mentorService;
    private final CourseTagService courseTagService;

    public SuggestionServiceImpl(CourseMentorService courseMentorService,
                                 EnrollmentService enrollmentService,
                                 MentorService mentorService,
                                 CourseTagService courseTagService) {
        this.courseMentorService = courseMentorService;
        this.enrollmentService = enrollmentService;
        this.mentorService = mentorService;
        this.courseTagService = courseTagService;
    }


    @Override
    public List<CourseMentor> getSuggestedCourses(SuggestionType type, UUID menteeId, int limit) {
        if (type == null) {
            return List.of();
        }

        return switch (type) {
            case POPULAR -> enrollmentService.getPopularCoursesForGuest(limit);
            case LATEST -> courseMentorService.findLatestCourse(limit);
            case INTEREST_BASED -> courseMentorService.getRecommendedCoursesByInterests(menteeId, limit);
            case HISTORY_BASED -> courseMentorService.getRecommendedByHistory(menteeId, limit);
            case BECAUSE_YOU_LEARNED -> {
                List<CourseMentor> inProgress = enrollmentService.getCourseInProgressMentee(menteeId);
                if (!inProgress.isEmpty()) {
                    int randomIndex = ThreadLocalRandom.current().nextInt(inProgress.size());
                    CourseMentor baseCourse = inProgress.get(randomIndex);
                    yield courseMentorService.getRelatedCoursesByTags(baseCourse.getCourse().getId(), menteeId, limit);
                } else {
                    yield List.of();
                }
            }
            default -> List.of();
        };
    }

    @Override
    public List<Mentor> getSuggestedMentors(SuggestionType type, UUID menteeId, int limit) {
        if (type == null) {
            return mentorService.getTopMentorsByRatingOrSessions(limit);
        }

        return switch (type) {
            case TOP_RATED, POPULAR -> mentorService.getTopMentorsByRatingOrSessions(limit);
            case INTEREST_BASED -> mentorService.findMentorsByMenteeInterest(menteeId, limit);
            default -> List.of();
        };
    }

    @Override
    public List<TagEnrollmentCountDTO> getSuggestedTags(SuggestionType type, int limit) {
        if (type == null) {
            return courseTagService.getTopTags(limit);
        }

        return switch (type) {
            case POPULAR, TRENDING -> courseTagService.getTopTags(limit);
            default -> List.of();
        };
    }
}
