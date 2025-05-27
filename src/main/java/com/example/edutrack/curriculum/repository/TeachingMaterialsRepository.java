package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeachingMaterialsRepository extends CrudRepository<TeachingMaterial, Integer> {
    List<TeachingMaterial> findByCourseId(UUID courseId);
    Course findCourseById(int courseId);

}
