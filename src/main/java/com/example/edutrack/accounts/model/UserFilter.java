package com.example.edutrack.accounts.model;

public class UserFilter {
        private String email;
        private String fullName;
        private Boolean isLocked;
        private Boolean isActive;

        public UserFilter() {
        }

        public UserFilter(String email, String fullName, Boolean isLocked, Boolean isActive) {
        this.email = email;
        this.fullName = fullName;
        this.isLocked = isLocked;
        this.isActive = isActive;
        }

        // Getters and setters
        public String getEmail() {
        return email;
        }

        public void setEmail(String email) {
        this.email = email;
        }

        public String getFullName() {
        return fullName;
        }

        public void setFullName(String fullName) {
        this.fullName = fullName;
        }

        public Boolean getIsLocked() {
        return isLocked;
        }

        public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
        }

        public Boolean getIsActive() {
        return isActive;
        }

        public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        }
}
