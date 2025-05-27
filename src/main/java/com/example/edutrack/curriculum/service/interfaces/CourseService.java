package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CourseService {

    Page<Course> findAll(Pageable pageable);


    Course findById(UUID id);

    UUID create(CourseFormDTO courseFormDTO);

    void update(UUID id, CourseFormDTO courseFormDTO);

    List<Course> getAll();

    List<Course> getFilteredCourses(String search,
                                    UUID mentorId,
                                    Boolean open,
                                    Date fromDate,
                                    Date toDate,
                                    String sortBy);
}
