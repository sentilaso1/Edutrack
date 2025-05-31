package com.example.edutrack.curriculum.repository;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicantsRepository extends JpaRepository<CourseMentor, UUID> {
    void deleteById(UUID id);

    @Query("SELECT cm.mentor FROM CourseMentor cm WHERE cm.course = :course AND cm.status = :status")
    List<Mentor> findMentorsByCourseAndStatus(@Param("course") Course course, @Param("status") ApplicationStatus status);

    int countByCourseAndStatus(Course course, ApplicationStatus status);

    Optional<CourseMentor> findById(UUID id);

    List<CourseMentor> findByMentorId(UUID mentorId);
}
