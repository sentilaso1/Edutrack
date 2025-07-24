package com.example.edutrack.accounts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.edutrack.accounts.model.ScheduledJob;

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
        Page<ScheduledJob> findByNameContainingIgnoreCase(String name, Pageable pageable);
        int countByActive(boolean active);
}
