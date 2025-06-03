package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
        Page<RequestLog> findAll(Pageable pageable);
}
