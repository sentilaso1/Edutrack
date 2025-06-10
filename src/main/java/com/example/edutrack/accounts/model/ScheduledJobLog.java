package com.example.edutrack.accounts.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "scheduled_job_logs")
public class ScheduledJobLog {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Long jobId;
        private LocalDateTime executedAt;
        private String status; // "SUCCESS", "FAILED"
        private String message;

        public ScheduledJobLog() {
        }

        public ScheduledJobLog(Long jobId, LocalDateTime executedAt, String status, String message) {
                this.jobId = jobId;
                this.executedAt = executedAt;
                this.status = status;
                this.message = message;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getJobId() {
                return jobId;
        }

        public void setJobId(Long jobId) {
                this.jobId = jobId;
        }

        public LocalDateTime getExecutedAt() {
                return executedAt;
        }

        public void setExecutedAt(LocalDateTime executedAt) {
                this.executedAt = executedAt;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }
}
