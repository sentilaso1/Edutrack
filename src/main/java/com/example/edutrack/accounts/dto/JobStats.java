package com.example.edutrack.accounts.dto;

public class JobStats {
        private int total;
        private int activeCount;
        private int inactiveCount;

        public JobStats() {
        }

        public JobStats(int total, int activeCount, int inactiveCount) {
                this.total = total;
                this.activeCount = activeCount;
                this.inactiveCount = inactiveCount;
        }
        
        public int getTotal() {
                return total;
        }

        public void setTotal(int total) {
                this.total = total;
        }

        public int getActiveCount() {
                return activeCount;
        }

        public void setActiveCount(int activeCount) {
                this.activeCount = activeCount;
        }

        public int getInactiveCount() {
                return inactiveCount;
        }

        public void setInactiveCount(int inactiveCount) {
                this.inactiveCount = inactiveCount;
        }
        
}
