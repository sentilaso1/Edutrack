package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;

import java.util.List;
import java.util.UUID;

public interface CourseTagService {
    List<CourseTag> findTagsByCourseId(UUID courseId);

    boolean existsByCourseAndTag(UUID courseId, int tagId);

    void addCourseTag(UUID courseId, int tagId);
}
