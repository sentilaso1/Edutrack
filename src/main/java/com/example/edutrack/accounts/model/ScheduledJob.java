package com.example.edutrack.accounts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_jobs")
public class ScheduledJob {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String cronExpression;
        private String description;
        private boolean active;
        private String tag;

        @Column(name = "last_run_time")
        private LocalDateTime lastRunTime;

        @Column(name = "last_status")
        private String lastStatus; 

        public ScheduledJob() {
        }

        public ScheduledJob(String name, String cronExpression, String description, boolean active, String tag) {
                this.name = name;
                this.cronExpression = cronExpression;
                this.description = description;
                this.active = active;
                this.tag = tag;
                this.lastRunTime = LocalDateTime.now();
                this.lastStatus = "Pending";
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getCronExpression() {
                return cronExpression;
        }

        public void setCronExpression(String cronExpression) {
                this.cronExpression = cronExpression;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public boolean isActive() {
                return active;
        }

        public void setActive(boolean active) {
                this.active = active;
        }

        public String getTag() {
                return tag;
        }

        public void setTag(String tag) {
                this.tag = tag;
        }

        public LocalDateTime getLastRunTime() {
                return lastRunTime;
        }

        public void setLastRunTime(LocalDateTime lastRunTime) {
                this.lastRunTime = lastRunTime;
        }

        public String getLastStatus() {
                return lastStatus;
        }

        public void setLastStatus(String lastStatus) {
                this.lastStatus = lastStatus;
        }
        
        
}
