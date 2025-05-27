package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.TagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> findTagsByCourseId(UUID courseId) {
        return tagRepository.findByCourseId(courseId);
    }
}
