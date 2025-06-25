package com.example.edutrack.accounts.dto;

import java.util.UUID;

public class TopMentorDTO {
        private UUID mentorId;
        private String mentorName;
        private Double totalRevenue;
        private String formattedRevenue;
        private Integer rank;

        public TopMentorDTO() {
        }

        public TopMentorDTO(UUID mentorId, String mentorName, Double totalRevenue, Integer rank) {
                this.mentorId = mentorId;
                this.mentorName = mentorName;
                this.totalRevenue = totalRevenue;
                this.rank = rank;
        }

        // Getters and Setters
        public UUID getMentorId() {
                return mentorId;
        }

        public void setMentorId(UUID mentorId) {
                this.mentorId = mentorId;
        }

        public String getMentorName() {
                return mentorName;
        }

        public void setMentorName(String mentorName) {
                this.mentorName = mentorName;
        }

        public Double getTotalRevenue() {
                return totalRevenue;
        }

        public void setTotalRevenue(Double totalRevenue) {
                this.totalRevenue = totalRevenue;
        }

        public String getFormattedRevenue() {
                return formattedRevenue;
        }

        public void setFormattedRevenue(String formattedRevenue) {
                this.formattedRevenue = formattedRevenue;
        }

        public Integer getRank() {
                return rank;
        }

        public void setRank(Integer rank) {
                this.rank = rank;
        }
}
