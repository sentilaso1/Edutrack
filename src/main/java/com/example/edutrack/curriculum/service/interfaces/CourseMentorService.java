package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CourseMentorService {

    Page<CourseMentor> findAlByOrderByCreatedDateAsc(Pageable pageable);

    Page<CourseMentor> findAlByOrderByCreatedDateDesc(Pageable pageable);

    Page<CourseMentor> findAlByOrderByTitleDesc(Pageable pageable);

    Page<CourseMentor> findAlByOrderByTitleAsc(Pageable pageable);

    Page<CourseMentor> findAll(Pageable pageable);

    List<Tag> findAllTags();

    List<Course> findAllCourses();

    CourseMentor findById(UUID id);

    List<CourseMentor> getCourseMentorByMentorId(UUID id);

    Page<CourseMentor> findFilteredCourseMentors(List<UUID> skillIds, List<Integer> subjectIds, Pageable pageable);

    List<CourseMentor> findByCourseId(UUID courseMentorId);

    List<CourseMentor> findLatestCourse(int maxCount);

    List<CourseMentor> getRecommendedCoursesByInterests(UUID menteeId, int limit);

    List<CourseMentor> getRelatedCoursesByTags(UUID courseMentorId, UUID menteeId, int limit);

    List<CourseMentor> getRecommendedCourseMentors(UUID menteeId, int limit);

    List<CourseMentor> getRecommendedByHistory(UUID menteeId, int limit);

    List<CourseMentor> getReviewablePairsForMentee(UUID menteeId);
}
