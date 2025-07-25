package com.example.edutrack.curriculum.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseMentorRepository extends JpaRepository<CourseMentor, CourseMentorId> {

    @Query("SELECT ct.tag FROM CourseMentor cm JOIN CourseTag ct ON ct.course = cm.course")
    List<Tag> findAllTags();

    @Query("SELECT cm.course FROM CourseMentor cm")
    List<Course> findAllCourses();

    @Query("""
                SELECT cm FROM CourseMentor cm
                WHERE
                    (:skillIds IS NULL OR cm.course.id IN :skillIds)
                AND
                    (:subjectIds IS NULL OR EXISTS (
                        SELECT 1 FROM CourseTag ct
                        WHERE ct.course = cm.course
                        AND ct.tag.id IN :subjectIds
                    ))
            """)
    Page<CourseMentor> findFilteredCourseMentors(
            @Param("skillIds") List<UUID> skillIds,
            @Param("subjectIds") List<Integer> subjectIds,
            Pageable pageable);

    @Query("""
                 SELECT cm FROM CourseMentor cm
                 WHERE
                     (:skillIds IS NULL OR cm.course.id IN :skillIds)
                 AND
                     (:search IS NULL OR LOWER(cm.course.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND cm.status = 'ACCEPTED'
                 AND
                     (:subjectIds IS NULL OR EXISTS (
                         SELECT 1 FROM CourseTag ct
                         WHERE ct.course = cm.course
                         AND ct.tag.id IN :subjectIds
                     ))
             """)
    Page<CourseMentor> findFilteredCourseMentors(
            @Param("skillIds") List<UUID> skillIds,
            @Param("subjectIds") List<Integer> subjectIds,
            Pageable pageable, String search);

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.createdDate DESC")
    Page<CourseMentor> findAlByOrderByCreatedDateDesc(
            Pageable pageable);

    @Query("SELECT cm FROM CourseMentor cm WHERE (:search IS NULL OR LOWER(cm.course.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND cm.status = 'ACCEPTED' ORDER BY cm.course.createdDate DESC")
    Page<CourseMentor> findAlByOrderByCreatedDateDesc(
            Pageable pageable, String search);

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.createdDate ASC")
    Page<CourseMentor> findAlByOrderByCreatedDateAsc(
            Pageable pageable);

    @Query("SELECT cm FROM CourseMentor cm WHERE (:search IS NULL OR LOWER(cm.course.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND cm.status = 'ACCEPTED' ORDER BY cm.course.createdDate ASC ")
    Page<CourseMentor> findAlByOrderByCreatedDateAsc(
            Pageable pageable, String search);

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.name ASC")
    Page<CourseMentor> findAlByOrderByTitleAsc(
            Pageable pageable);

    @Query("SELECT cm FROM CourseMentor cm WHERE (:search IS NULL OR LOWER(cm.course.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND cm.status = 'ACCEPTED' ORDER BY cm.course.name ASC")
    Page<CourseMentor> findAlByOrderByTitleAsc(
            Pageable pageable, String search);

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.name DESC")
    Page<CourseMentor> findAlByOrderByTitleDesc(
            Pageable pageable);

    @Query("SELECT cm FROM CourseMentor cm WHERE (:search IS NULL OR LOWER(cm.course.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND cm.status = 'ACCEPTED' ORDER BY cm.course.name DESC")
    Page<CourseMentor> findAlByOrderByTitleDesc(
            Pageable pageable, String search);

    List<CourseMentor> findByCourseId(UUID courseMentorId);

    @Query("SELECT cm.course FROM CourseMentor cm WHERE cm.mentor.id = :mentorId")
    List<Course> findCoursesByMentorId(@Param("mentorId") UUID mentorId);

    @Query("SELECT cm FROM CourseMentor cm WHERE cm.status = 'ACCEPTED' ORDER BY cm.appliedDate DESC")
    List<CourseMentor> findLatestCourse(Pageable pageable);

    @Query("""
                SELECT DISTINCT cm FROM CourseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course = c
                JOIN Tag t ON ct.tag = t
                WHERE LOWER(t.title) IN :interests
            """)
    List<CourseMentor> findByTagsMatchingInterests(@Param("interests") List<String> interests, Pageable pageable);

    @Query("""
                SELECT DISTINCT cm FROM CourseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course = c
                JOIN ct.tag t
                WHERE LOWER(t.title) IN :tagTitles
                AND cm.course.id NOT IN (
                    SELECT e.courseMentor.course.id FROM Enrollment e
                    WHERE e.mentee.id = :menteeId
                )
            """)
    List<CourseMentor> findRelatedByTagsAndNotEnrolled(
            @Param("tagTitles") List<String> tagTitles,
            @Param("menteeId") UUID menteeId,
            Pageable pageable);

    @Query("""
                SELECT cm FROM CourseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course.id = c.id
                WHERE LOWER(ct.tag.title) IN :tags
            """)
    List<CourseMentor> findRelatedByTags(@Param("tags") List<String> tags,
                                         Pageable pageable);

    @Query("""
            SELECT DISTINCT cm FROM CourseMentor cm
            JOIN cm.course c
            JOIN CourseTag ct ON ct.course = c
             WHERE (
                  LOWER(ct.tag.title) IN :keywords
                  OR LOWER(cm.mentor.expertise) IN :keywords
              )
              AND cm.course.id NOT IN (
                  SELECT e.courseMentor.course.id FROM Enrollment e
                  WHERE e.mentee.id = :menteeId
              )
            """)
    List<CourseMentor> findRecommendedByKeywords(
            @Param("keywords") List<String> keywords,
            @Param("menteeId") UUID menteeId,
            Pageable pageable);

    Optional<CourseMentor> findByMentorAndCourse(Mentor mentor, Course course);

    List<CourseMentor> findAllByMentor(Mentor mentor);

    @Query("SELECT COUNT(cm) FROM CourseMentor cm WHERE cm.status = 'ACCEPTED'")
    Long getAcceptedCourseMentorCount();

    @Query("SELECT cm FROM CourseMentor cm WHERE cm.status = 'ACCEPTED' AND cm.appliedDate >= :startDate")
    List<CourseMentor> getActiveCourseMentorsFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT AVG(cm.price) FROM CourseMentor cm WHERE cm.status = 'ACCEPTED'")
    Double getAveragePrice();

    @Query("SELECT COUNT(DISTINCT cm.mentor.id) FROM CourseMentor cm WHERE cm.status = 'ACCEPTED'")
    Long getActiveMentorCount();

    List<CourseMentor> findByMentorId(UUID mentorId);
    List<CourseMentor> findByMentorIdAndStatus(UUID mentorId, ApplicationStatus status);

    Optional<CourseMentor> findByCourse_IdAndMentor_Id(UUID courseId, UUID mentorId);

    List<CourseMentor> findByCourse_Id(UUID courseId);

    @Query("SELECT COALESCE(CAST(SUM(t.amount) AS long), 0) " +
                    "FROM Transaction t " +
                    "JOIN Enrollment e ON e.transaction = t " +
                    "JOIN CourseMentor cm ON cm = e.courseMentor " +
                    "WHERE cm.mentor.id = :mentorId " +
                    "AND t.status = 'COMPLETED' " +
                    "AND cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED")
    Long getTotalIncomeByMentorId(@Param("mentorId") UUID mentorId);

    @Query("SELECT COALESCE(CAST(SUM(t.amount) / SUM(e.totalSlots) AS long), 0) " +
                    "FROM Transaction t " +
                    "JOIN Enrollment e ON e.transaction = t " +
                    "JOIN CourseMentor cm ON cm = e.courseMentor " +
                    "WHERE cm.mentor.id = :mentorId " +
                    "AND t.status = 'COMPLETED' " +
                    "AND cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED")
    Long getIncomePerSlot(@Param("mentorId") UUID mentorId);

    @Query("SELECT COALESCE(CAST(SUM(t.amount) AS long), 0) " +
                    "FROM Transaction t " +
                    "JOIN Enrollment e ON e.transaction = t " +
                    "JOIN CourseMentor cm ON cm = e.courseMentor " +
                    "WHERE cm.mentor.id = :mentorId " +
                    "AND t.status = 'COMPLETED' " +
                    "AND t.createdDate >= :startDate " +
                    "AND cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED " +
                    "GROUP BY YEAR(t.createdDate), MONTH(t.createdDate) " +
                    "ORDER BY YEAR(t.createdDate) DESC, MONTH(t.createdDate) DESC")
    List<Long> getMonthlyIncomeList(@Param("mentorId") UUID mentorId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(CAST(SUM(t.amount) AS long), 0) " +
                    "FROM Transaction t " +
                    "JOIN Enrollment e ON e.transaction = t " +
                    "JOIN CourseMentor cm ON cm = e.courseMentor " +
                    "WHERE cm.mentor.id = :mentorId " +
                    "AND t.status = 'COMPLETED' " +
                    "AND YEAR(t.createdDate) = :year " +
                    "AND MONTH(t.createdDate) = :month " +
                    "AND cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED")
    Long getCurrentMonthIncome(@Param("mentorId") UUID mentorId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(CAST(SUM(t.amount) AS long), 0) " +
                    "FROM Transaction t " +
                    "JOIN Enrollment e ON e.transaction = t " +
                    "JOIN CourseMentor cm ON cm = e.courseMentor " +
                    "WHERE cm.mentor.id = :mentorId " +
                    "AND t.status = 'COMPLETED' " +
                    "AND YEAR(t.createdDate) = :year " +
                    "AND MONTH(t.createdDate) = :month " +
                    "AND cm.status = com.example.edutrack.curriculum.model.ApplicationStatus.ACCEPTED")
    Long getLastMonthIncome(@Param("mentorId") UUID mentorId, @Param("month") int month, @Param("year") int year);

    @Query("""
                SELECT cm
                FROM CourseMentor cm
                WHERE cm.id NOT IN :excludedIds
                ORDER BY FUNCTION('RAND')
            """)
    List<CourseMentor> findRandomCourseMentorsExcluding(@Param("excludedIds") List<UUID> excludedIds, Pageable pageable);

    Page<CourseMentor> findByMentorId(UUID mentorId, Pageable pageable);

    boolean existsByCourseId(UUID courseId);

    @Query("SELECT cm FROM CourseMentor cm WHERE cm.id = :courseMentorId")
    CourseMentor findById(UUID courseMentorId);

    List<CourseMentor> findByCourse_IdAndStatus(UUID courseId, ApplicationStatus status);

    boolean existsByMentorIdAndCourseId(UUID userId, UUID courseId);

    Optional<CourseMentor> findByMentorIdAndCourseId(UUID userId, UUID courseId);
    long countByMentorAndStatus(Mentor mentor, ApplicationStatus status);

    @Query("""
        SELECT COUNT(e) > 0 FROM Enrollment e
        WHERE e.courseMentor = :courseMentor
          AND e.mentee = :mentee
          AND e.status = 'PENDING'
        """)
    boolean alreadyHasPendingEnrollment(@Param("courseMentor") CourseMentor courseMentor, @Param("mentee") Mentee mentee);
}

