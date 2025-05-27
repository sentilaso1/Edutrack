package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.CourseTagsRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseTagServiceImpl implements CourseTagService {
    private final CourseTagsRepository courseTagsRepository;
    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;

    @Autowired
    public CourseTagServiceImpl(CourseTagsRepository courseTagsRepository,
                                CourseRepository courseRepository,
                                TagRepository tagRepository) {
        this.courseTagsRepository = courseTagsRepository;
        this.courseRepository = courseRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<CourseTag> findTagsByCourseId(UUID courseId) {
        return courseTagsRepository.findByCourseId(courseId);
    }

    @Override
    public boolean existsByCourseAndTag(UUID courseId, int tagId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (course == null || tag == null) return false;
        return courseTagsRepository.existsByCourseAndTag(course, tag);
    }

    @Override
    public void addCourseTag(UUID courseId, int tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        if (!courseTagsRepository.existsByCourseAndTag(course, tag)) {
            CourseTag courseTag = new CourseTag(course, tag);
            courseTagsRepository.save(courseTag);
        }
    }
}
