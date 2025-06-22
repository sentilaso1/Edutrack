package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("""
            SELECT f FROM Feedback f 
            WHERE f.mentee.id = :menteeId
            AND(:keyword is null OR LOWER(f.courseMentor.course.name) LIKE LOWER(CONCAT('%',:keyword,'%'))
                                  OR LOWER(f.courseMentor.mentor.fullName) LIKE LOWER(CONCAT('%',:keyword,'%'))) 
            AND (:rating IS NULL OR (f.rating >= :rating AND f.rating < (:rating + 1)))
            ORDER BY f.createdDate DESC
            """)
    Page<Feedback> findFilteredFeedbacksByMentee(@Param("menteeId") UUID menteeId,
                                                 @Param("keyword") String keyword,
                                                 @Param("rating") Integer rating,
                                                 Pageable pageable);
}
