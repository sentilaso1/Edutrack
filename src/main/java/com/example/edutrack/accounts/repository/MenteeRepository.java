package com.example.edutrack.accounts.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.edutrack.accounts.model.Mentee;

public interface MenteeRepository extends JpaRepository<Mentee, UUID> {
    Optional<Mentee> findById(UUID id);

}
