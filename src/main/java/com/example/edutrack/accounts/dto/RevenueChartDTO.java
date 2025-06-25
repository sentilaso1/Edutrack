package com.example.edutrack.accounts.dto;

import java.time.LocalDate;

public class RevenueChartDTO {
        private LocalDate date;
        private Double revenue;
        private String label;
        
        public RevenueChartDTO() {}
        
        public RevenueChartDTO(LocalDate date, Double revenue, String label) {
                this.date = date;
                this.revenue = revenue;
                this.label = label;
        }
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public Double getRevenue() { return revenue; }
        public void setRevenue(Double revenue) { this.revenue = revenue; }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
}
