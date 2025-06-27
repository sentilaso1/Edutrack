package com.example.edutrack.accounts.dto;

import java.util.List;

public class IncomeStatsDTO {
        private long totalIncome;
        private List<Long> incomeOverTime;
        private long incomePerSlot;
        private double percentChange;

        
        // Constructors
        public IncomeStatsDTO() {
        }

        public IncomeStatsDTO(long totalIncome, List<Long> incomeOverTime, long incomePerSlot, double percentChange) {
                this.totalIncome = totalIncome;
                this.incomeOverTime = incomeOverTime;
                this.incomePerSlot = incomePerSlot;
                this.percentChange = percentChange;
        }

        // Getters and Setters
        public double getPercentChange() {
                return percentChange;
        }

        public void setPercentChange(double percentChange) {
                this.percentChange = percentChange;
        }

        public long getTotalIncome() {
                return totalIncome;
        }

        public void setTotalIncome(long totalIncome) {
                this.totalIncome = totalIncome;
        }

        public List<Long> getIncomeOverTime() {
                return incomeOverTime;
        }

        public void setIncomeOverTime(List<Long> incomeOverTime) {
                this.incomeOverTime = incomeOverTime;
        }

        public long getIncomePerSlot() {
                return incomePerSlot;
        }

        public void setIncomePerSlot(long incomePerSlot) {
                this.incomePerSlot = incomePerSlot;
        }
}
