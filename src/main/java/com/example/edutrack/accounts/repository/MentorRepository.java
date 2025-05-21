package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
}
