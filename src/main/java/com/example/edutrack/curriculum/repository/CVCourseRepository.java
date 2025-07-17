package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.CVCourse;
import com.example.edutrack.curriculum.model.CVCourseId;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CVCourseRepository extends JpaRepository<CVCourse, CVCourseId> {
    List<CVCourse> findByIdCvId(UUID cvId);
    @Query("SELECT COUNT(cvc) FROM CVCourse cvc WHERE cvc.course = :course AND cvc.cv.status = :status")
    int countByCourseAndCVStatus(@Param("course") Course course, @Param("status") String status);
    boolean existsByCourse_IdAndCv_StatusNot(UUID courseId, String status);
}
