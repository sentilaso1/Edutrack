package com.example.edutrack.accounts.dto;

public class UserStats {
        private int total;
        private int active;
        private int locked;

        public UserStats() {
        }
        public UserStats(int total, int active, int locked) {
                this.total = total;
                this.active = active;
                this.locked = locked;
        }
        public int getTotal() {
                return total;
        }
        public void setTotal(int total) {
                this.total = total;
        }
        public int getActive() {
                return active;
        }
        public void setActive(int active) {
                this.active = active;
        }
        public int getLocked() {
                return locked;
        }
        public void setLocked(int locked) {
                this.locked = locked;
        }
}
