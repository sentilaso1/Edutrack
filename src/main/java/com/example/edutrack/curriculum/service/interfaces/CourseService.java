package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CourseService {

    Page<Course> findAll(Pageable pageable);


    List<Course> findAll();

    void save(Course course);

    Course findById(UUID id);

    UUID create(CourseFormDTO courseFormDTO);

    void update(UUID id, CourseFormDTO courseFormDTO);

    List<Course> getAll();

    Page<Course> getAll(Pageable pageable);

    Page<Course> findFilteredCourses(
            List<UUID> skillIds,
            List<Integer> subjectIds,
            Pageable pageable
    );

    Page<Course> getFilteredCourses(String search,
                                    String mentorSearch,
                                    Boolean open,
                                    Date fromDate,
                                    Date toDate,
                                    String sortBy,
                                    Pageable pageable);

    Map<UUID, Integer> getPendingApplicantCountForCourses(List<Course> courses);

    void delete(UUID id);

    public Map<UUID, List<Mentor>> getAcceptedMentorsForCourses(List<Course> courses);

    @Transactional
    void deleteCourseWithRelatedData(UUID courseId);

    Page<Course> findAllExcludingIds(List<UUID> excludeIds, Pageable pageable);
}
