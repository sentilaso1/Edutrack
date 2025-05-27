package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.TagDTO;

import java.util.List;
import java.util.UUID;

public interface CourseTagService {
    List<TagDTO> findTagsByCourseId(UUID courseId);

    boolean existsByCourseAndTag(UUID courseId, int tagId);

    void addCourseTag(UUID courseId, int tagId);
}
