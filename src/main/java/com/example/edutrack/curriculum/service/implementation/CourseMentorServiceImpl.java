package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
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

    @Autowired
    public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository) {
        this.courseMentorRepository = courseMentorRepository;
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


    public Page<CourseMentor> findFilteredCourseMentors(List<UUID> skillIds, List<Integer> subjectIds, Pageable pageable) {
        return courseMentorRepository.findFilteredCourseMentors(skillIds, subjectIds, pageable);
    }

    public List<CourseMentor> findByCourseId(UUID courseMentorId) {
        return courseMentorRepository.findByCourseId(courseMentorId);
    }

}
