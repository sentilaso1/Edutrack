package com.example.edutrack.profiles.repository;

import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CvRepository extends JpaRepository<CV, UUID> {
    List<CV> findAllByOrderByCreatedDateDesc();

    Page<CV> findAllByOrderByCreatedDateAsc(Pageable pageable);

    Page<CV> findAllByStatusOrderByCreatedDateDesc(Pageable pageable, String status);

    Page<CV> findAllByStatusOrderByCreatedDateAsc(Pageable pageable, String status);

    Optional<CV> findByUserId(UUID userId);

    List<CV> findByStatus(String status);


    @Query(value = """
            SELECT * FROM cv
            WHERE status IN ('pending', 'aiapproved', 'approved', 'rejected')
            ORDER BY
              CASE status
                WHEN 'pending' THEN 1
                WHEN 'aiapproved' THEN 2
                WHEN 'approved' THEN 3
                WHEN 'rejected' THEN 4
              END,
              created_date DESC
            """, nativeQuery = true)
    Page<CV> findAllStatusDateDesc(Pageable pageable);


    @Query(value = """
            SELECT * FROM cv
            WHERE status IN ('pending', 'aiapproved', 'approved', 'rejected')
            ORDER BY
              CASE status
                WHEN 'pending' THEN 1
                WHEN 'aiapproved' THEN 2
                WHEN 'approved' THEN 3
                WHEN 'rejected' THEN 4
              END,
              created_date
            """, nativeQuery = true)
    Page<CV> findAllStatusDateAsc(Pageable pageable);

    @Query("SELECT DISTINCT cv.skills FROM CV cv")
    List<String> findAllSkills();

    @Query("SELECT c FROM CV c WHERE c.user.id = :mentorId AND c.status = 'approved'")
    CV findByMentorId(UUID mentorId);
}
