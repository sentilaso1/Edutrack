package com.example.edutrack.accounts.dto;

import java.time.LocalDateTime;

public class ScheduledJobDTO {
        private Long id;
        private String name;
        private String cronExpression;
        private String description;
        private boolean active;
        private String tag;
        private String lastStatus;
        private LocalDateTime lastRunTime;

        public ScheduledJobDTO() {
        }

        public ScheduledJobDTO(Long id, String name, String cronExpression, String description, boolean active,
                        String tag, String lastStatus, LocalDateTime lastRunTime) {
                this.id = id;
                this.name = name;
                this.cronExpression = cronExpression;
                this.description = description;
                this.active = active;
                this.tag = tag;
                this.lastStatus = lastStatus;
                this.lastRunTime = lastRunTime;
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

        public String getLastStatus() {
                return lastStatus;
        }

        public void setLastStatus(String lastStatus) {
                this.lastStatus = lastStatus;
        }

        public LocalDateTime getLastRunTime() {
                return lastRunTime;
        }

        public void setLastRunTime(LocalDateTime lastRunTime) {
                this.lastRunTime = lastRunTime;
        }
}
