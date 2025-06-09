package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.ScheduledJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduledJobLogRepository extends JpaRepository<ScheduledJobLog, Long> {
        List<ScheduledJobLog> findByJobIdOrderByExecutedAtDesc(Long jobId);	
}
