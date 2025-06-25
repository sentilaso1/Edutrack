package com.example.edutrack.accounts.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.edutrack.accounts.model.Mentee;

public interface MenteeRepository extends JpaRepository<Mentee, UUID> {
    Optional<Mentee> findById(UUID id);

    Optional<Mentee> findByEmail(String email);

    @Query("SELECT COUNT(m) FROM Mentee m WHERE m.createdDate >= :startDate")
    Long getNewMenteeCountFromDate(@Param("startDate") LocalDateTime startDate);

}
