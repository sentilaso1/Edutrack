package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseMentorService {
    Page<CourseMentor> findAll(Pageable pageable);

    List<Tag> findAllTags();

    List<Course> findAllCourses();
}
