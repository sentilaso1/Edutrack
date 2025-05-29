package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.TeachingMaterial;

import java.util.List;
import java.util.UUID;

public interface TeachingMaterialsService {
    List<TeachingMaterial> findByCourseId(UUID courseId);

    TeachingMaterial findById(int id);

    void deleteById(int id);

    Course findCourseByMaterialId(int id);
}
