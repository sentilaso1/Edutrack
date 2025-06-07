package com.example.edutrack.accounts.dto;

public class UserStatsDTO {
        private long total;
        private long active;
        private long locked;

        public UserStatsDTO(long total, long active, long locked) {
                this.total = total;
                this.active = active;
                this.locked = locked;
        }

        public long getTotal() {
                return total;
        }

        public long getActive() {
                return active;
        }

        public long getLocked() {
                return locked;
        }
}
