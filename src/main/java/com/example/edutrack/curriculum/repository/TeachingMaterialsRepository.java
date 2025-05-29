package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeachingMaterialsRepository extends JpaRepository<TeachingMaterial, Integer> {
    List<TeachingMaterial> findByCourseId(UUID courseId);
    @Query("SELECT t.course FROM TeachingMaterial t WHERE t.id = :teachingMaterialId")
    Course findCourseByTeachingMaterialId(@Param("teachingMaterialId") int teachingMaterialId);


}
