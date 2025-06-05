package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
        Page<RequestLog> findAll(Pageable pageable);
        @Query("SELECT r FROM RequestLog r " +
        "WHERE (:ip IS NULL OR r.ip LIKE %:ip%) " +
        "AND (:method IS NULL OR r.method = :method) " +
        "AND (:uri IS NULL OR r.uri LIKE %:uri%)")
        Page<RequestLog> filterLogs(@Param("ip") String ip,
                                        @Param("method") String method,
                                        @Param("uri") String uri,
                                        Pageable pageable);
}
