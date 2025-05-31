package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {

    // Receiving mentor list with certain conditions
    @Query("SELECT m FROM Mentor m " +
            "WHERE (:name IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:expertise IS NULL OR LOWER(m.expertise) LIKE LOWER(CONCAT('%', :expertise, '%'))) " +
            "AND (:rating IS NULL OR m.rating >= :rating) " +
            "AND (:totalSessions IS NULL OR m.totalSessions >= :totalSessions) " +
            "AND (:isAvailable IS NULL OR m.isAvailable = :isAvailable)")


    List<Mentor> searchMentors(
            @Param("name") String name,
            @Param("expertise") String expertise,
            @Param("rating") Double rating,
            @Param("totalSessions") Integer totalSessions,
            @Param("isAvailable") Boolean isAvailable
    );

    @Query("SELECT ct.tag FROM Mentor m " +
           "JOIN CourseMentor c ON c.mentor = m " +
           "JOIN CourseTag ct ON ct.course = c.course " +
           "WHERE m.id = :id")
    List<Tag> findTagsByMentorId(@Param("id") UUID id);

    @Query("SELECT cv FROM CV cv WHERE cv.user.id = :mentorId")
    Optional<CV> findCVByMentorId(UUID mentorId);
}
