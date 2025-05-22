package com.example.edutrack.curriculum.service;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.TeachingMaterials;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.CourseTagsRepository;
import com.example.edutrack.curriculum.repository.TeachingMaterialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeachingMaterialsRepository teachingMaterialsRepository;
    private final CourseTagsRepository courseTagsRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TeachingMaterialsRepository teachingMaterialsRepository, CourseTagsRepository courseTagsRepository) {
        this.courseRepository = courseRepository;
        this.teachingMaterialsRepository = teachingMaterialsRepository;
        this.courseTagsRepository = courseTagsRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(UUID id) {
        return courseRepository.findById(id).orElseThrow( () -> new RuntimeException("Course Not Found"));
    }

    //Dang lam not
    public UUID create(CourseFormDTO courseFormDTO) {
        Course course = new Course();
        course.setName(courseFormDTO.getName());
        course.setDescription(courseFormDTO.getDescription());
        courseRepository.save(course);

        if (courseFormDTO.getTagIds() != null) {
            for (Integer tagId : courseFormDTO.getTagIds()) {
                CourseTag ct = new CourseTag();
                ct.setCourse(course);
                ct.getTag().setId(tagId);
                courseTagsRepository.save(ct);
            }
        }

        return course.getId();


    }

}
