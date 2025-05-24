package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
