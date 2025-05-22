package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.repository.CourseTagsRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseTagServiceImpl implements CourseTagService {
    private final CourseTagsRepository courseTagsRepository;
    public CourseTagServiceImpl(CourseTagsRepository courseTagsRepository) {
        this.courseTagsRepository = courseTagsRepository;
    }
    public List<TagDTO> findTagsByCourseId(UUID courseId) {
        List<CourseTag> courseTagList = courseTagsRepository.findByCourseId(courseId);
        return courseTagList.stream().map(tag -> new TagDTO(tag.getTag().getTitle(), tag.getTag().getDescription())).collect(Collectors.toList());
    }

}
