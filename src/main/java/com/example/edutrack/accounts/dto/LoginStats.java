package com.example.edutrack.accounts.dto;

public class LoginStats {
        private int today;
        private int week;
        private int month;

        public LoginStats() {
        }

        public LoginStats(int today, int week, int month) {
                this.today = today;
                this.week = week;
                this.month = month;
        }

        public int getToday() {
                return today;
        }

        public void setToday(int today) {
                this.today = today;
        }

        public int getWeek() {
                return week;
        }

        public void setWeek(int week) {
                this.week = week;
        }

        public int getMonth() {
                return month;
        }

        public void setMonth(int month) {
                this.month = month;
        }
}
