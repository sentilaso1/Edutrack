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

    /**
     * Tính tổng thu nhập của mentor từ các transaction đã hoàn thành
     */
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            JOIN Enrollment e ON e.transaction.id = t.id
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND t.status = 'COMPLETED'
            AND e.status = 'APPROVED'
            """)
    Long getTotalIncomeByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Tính thu nhập theo từng tháng trong 12 tháng gần nhất
     */
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            JOIN Enrollment e ON e.transaction.id = t.id
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND t.status = 'COMPLETED'
            AND e.status = 'APPROVED'
            AND EXTRACT(YEAR FROM t.createdDate) = :year
            AND EXTRACT(MONTH FROM t.createdDate) = :month
            """)
    Long getMonthlyIncome(@Param("mentorId") UUID mentorId,
                          @Param("year") int year,
                          @Param("month") int month);

    /**
     * Tính tổng số slot đã dạy của mentor
     */
    @Query("""
            SELECT COALESCE(SUM(e.totalSlots), 0)
            FROM Enrollment e
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND e.status = 'APPROVED'
            """)
    Long getTotalSlotsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Lấy danh sách thu nhập 12 tháng gần nhất (dùng cho biểu đồ)
     */
    @Query(value = """
            WITH RECURSIVE months AS (
                SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 11 MONTH), '%Y-%m-01') as month_date
                UNION ALL
                SELECT DATE_ADD(month_date, INTERVAL 1 MONTH)
                FROM months
                WHERE month_date < DATE_FORMAT(CURDATE(), '%Y-%m-01')
            )
            SELECT
                COALESCE(SUM(t.amount), 0) as monthly_income
            FROM months m
            LEFT JOIN transactions t ON DATE_FORMAT(t.created_date, '%Y-%m') = DATE_FORMAT(m.month_date, '%Y-%m')
                AND t.status = 'COMPLETED'
                AND EXISTS (
                    SELECT 1 FROM enrollments e
                    JOIN course_mentor cm ON e.course_mentor_id = cm.id
                    WHERE e.transaction_id = t.id
                    AND cm.mentor_user_id = :mentorId
                    AND e.status = 'APPROVED'
                )
            GROUP BY m.month_date
            ORDER BY m.month_date
            """, nativeQuery = true)
    List<Long> getMonthlyIncomeList(@Param("mentorId") UUID mentorId);

    /**
     * Tính thu nhập trung bình mỗi slot
     */
    @Query("""
            SELECT
                CASE
                    WHEN COALESCE(SUM(e.totalSlots), 0) = 0 THEN 0
                    ELSE COALESCE(SUM(t.amount), 0) / COALESCE(SUM(e.totalSlots), 1)
                END
            FROM Transaction t
            JOIN Enrollment e ON e.transaction.id = t.id
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND t.status = 'COMPLETED'
            AND e.status = 'APPROVED'
            """)
    Long getIncomePerSlot(@Param("mentorId") UUID mentorId);

    /**
     * Lấy thống kê tổng hợp cho mentor
     */
    @Query("""
            SELECT new map(
                COALESCE(SUM(t.amount), 0) as totalIncome,
                COALESCE(SUM(e.totalSlots), 0) as totalSlots,
                CASE
                    WHEN COALESCE(SUM(e.totalSlots), 0) = 0 THEN 0
                    ELSE COALESCE(SUM(t.amount), 0) / COALESCE(SUM(e.totalSlots), 1)
                END as incomePerSlot
            )
            FROM Transaction t
            JOIN Enrollment e ON e.transaction.id = t.id
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND t.status = 'COMPLETED'
            AND e.status = 'APPROVED'
            """)
    java.util.Map<String, Long> getIncomeStatsSummary(@Param("mentorId") UUID mentorId);

    // Lấy income tháng này
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            JOIN Enrollment e ON e.transaction.id = t.id
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND t.status = 'COMPLETED'
            AND e.status = 'APPROVED'
            AND DATE_FORMAT(t.createdDate, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')
            """)
    Long getCurrentMonthIncome(@Param("mentorId") UUID mentorId);

    // Lấy thu nhập tháng trước
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            JOIN Enrollment e ON e.transaction.id = t.id
            JOIN CourseMentor cm ON e.courseMentor.id = cm.id
            WHERE cm.mentor.id = :mentorId
            AND t.status = 'COMPLETED'
            AND e.status = 'APPROVED'
            AND FUNCTION('YEAR', t.createdDate) = :lastMonthYear
            AND FUNCTION('MONTH', t.createdDate) = :lastMonth
            """)
    Long getLastMonthIncome(@Param("mentorId") UUID mentorId,
                            @Param("lastMonthYear") int lastMonthYear,
                            @Param("lastMonth") int lastMonth);

    List<CourseMentor> findByMentor_Id(UUID mentorId);

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

    @Query("SELECT COUNT(DISTINCT cm.course) FROM CourseMentor cm WHERE cm.status = 'ACCEPTED'")
    long countDistinctAcceptedCourses();
}

