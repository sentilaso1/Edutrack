package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagService {
    List<Tag> findAll();
    List<Tag> findTagsByCourseId(UUID courseId);
    Optional<Tag> findById(Integer id);
    List<Optional<Tag>> findById(List<Integer> ids);
}
