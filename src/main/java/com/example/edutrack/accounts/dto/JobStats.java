package com.example.edutrack.accounts.dto;

public class JobStats {
        private int total;
        private int success;
        private int failed;

        public JobStats() {
        }

        public JobStats(int total, int success, int failed) {
                this.total = total;
                this.success = success;
                this.failed = failed;
        }
        
        public int getTotal() {
                return total;
        }

        public void setTotal(int total) {
                this.total = total;
        }

        public int getSuccess() {
                return success;
        }

        public void setSuccess(int success) {
                this.success = success;
        }

        public int getFailed() {
                return failed;
        }

        public void setFailed(int failed) {
                this.failed = failed;
        }
        
}
