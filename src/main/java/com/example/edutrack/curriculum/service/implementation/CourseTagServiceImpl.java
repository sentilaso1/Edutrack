package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.TagEnrollmentCountDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.CourseTagsRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public void deleteCourseTag(UUID courseId, int tagId) {
        courseTagsRepository.deleteByCourseIdAndTagId(courseId, tagId);
    }

    @Override
    public List<TagEnrollmentCountDTO> getTopTags(int limit){
        List<Object[]> topTagsRaw = courseTagsRepository.findTopTagsByEnrollment(PageRequest.of(0, limit));
        List<TagEnrollmentCountDTO> topTags = new java.util.ArrayList<>(topTagsRaw.stream()
                .map(obj -> new TagEnrollmentCountDTO((Tag) obj[0], (Long) obj[1]))
                .toList());

        int remaining = Math.max(0, limit - topTags.size());

        if (remaining > 0) {
            List<Integer> excludeIds = topTags.stream()
                    .map(dto -> dto.getTag().getId())
                    .toList();

            List<Tag> fallbackTags = courseTagsRepository.findRandomTagsExcluding(excludeIds, PageRequest.of(0, remaining));

            List<TagEnrollmentCountDTO> fallbackDtos = fallbackTags.stream()
                    .map(tag -> new TagEnrollmentCountDTO(tag, 0L))
                    .toList();

            topTags.addAll(fallbackDtos);
        }

        return topTags;
    }

    @Override
    public List<Tag> getAllTags(){
        return courseTagsRepository.findDistinctTagsFromCourses();
    }



}
