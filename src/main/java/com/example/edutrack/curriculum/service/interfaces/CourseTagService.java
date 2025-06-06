package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.dto.TagEnrollmentCountDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;

import java.util.List;
import java.util.UUID;

public interface CourseTagService {
    boolean existsByCourseAndTag(UUID courseId, int tagId);
    void addCourseTag(UUID courseId, int tagId);

    List<TagEnrollmentCountDTO> getTopTags(int maxCount);

    List<Tag> getAllTags();
}
