package com.example.edutrack.curriculum.service;

import com.example.edutrack.curriculum.model.Tags;
import com.example.edutrack.curriculum.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    public List<Tags> findAll() {
        return tagRepository.findAll();
    }
}
