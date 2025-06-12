package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Pageable;
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
            "AND (:rating IS NULL OR m.rating >= :rating) " +
            "AND (:totalSessions IS NULL OR m.totalSessions >= :totalSessions) " +
            "AND (:isAvailable IS NULL OR m.isAvailable = :isAvailable)")
    List<Mentor> searchMentorsBasic(
            @Param("name") String name,
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

    @Query("""
                SELECT DISTINCT m
                FROM Mentor m
                JOIN CourseMentor cm ON cm.mentor = m
                WHERE cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED
                AND (m.rating IS NOT NULL OR m.totalSessions > 0)
                ORDER BY m.rating DESC NULLS LAST, m.totalSessions DESC
            """)
    List<Mentor> findTopActiveMentors(Pageable pageable);

    @Query("""
                SELECT DISTINCT m FROM Mentor m
                WHERE LOWER(m.expertise) LIKE CONCAT('%', :keyword, '%')
            """)
    List<Mentor> findByExpertiseKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("""
                SELECT DISTINCT cm.mentor.expertise FROM Enrollment e
                JOIN e.courseMentor cm
                WHERE e.mentee.id = :menteeId and e.status = 'ACCEPTED'
            """)
    List<String> findExpertiseOfMentorsByMentee(@Param("menteeId") UUID menteeId);

    @Query("SELECT COUNT(DISTINCT e.courseMentor.mentor.id) FROM Enrollment e WHERE e.mentee.id = :menteeId")
    int countMentorsByMenteeId(@Param("menteeId") UUID menteeId);
    Optional<Mentor> findByEmail(String email);
}
