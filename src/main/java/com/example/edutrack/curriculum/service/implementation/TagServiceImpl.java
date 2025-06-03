package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.TagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<Tag> findById(Integer id) {
        return tagRepository.findById(id);
    }

    @Override
    public List<Optional<Tag>> findById(List<Integer> ids) {
        List<Optional<Tag>> tags = new ArrayList<>();
        for (Integer id : ids) {
            tags.add(tagRepository.findById(id));
        }
        return tags;
    }
}
