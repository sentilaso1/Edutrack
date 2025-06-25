package com.example.edutrack.accounts.dto;

import java.util.List;

public class ManagerStatsDTO {
        private Double totalRevenue;
        private Double revenueGrowth;
        private Integer totalMentors;
        private Integer mentorGrowth;
        private Integer totalStudents;
        private Integer studentGrowth;
        private Integer activeCourses;
        private Double satisfactionRate;
        private Double avgRevenuePerMentor;
        private Double avgStudentsPerMentor;
        private Double avgRating;
        private Double netProfit;
        private Double netProfitGrowth;
        private Double operatingCost;
        private Double operatingCostGrowth;
        private Double avgROI;
        private Double roiGrowth;
        private Double churnRate;
        private Double churnGrowth;
        private Double cac;
        private Double cacGrowth;
        private Double ltv;
        private Double ltvGrowth;

        private List<RevenueChartDTO> revenueChart;
        private List<TopMentorDTO> topMentors;

        // Constructors
        public ManagerStatsDTO() {
        }

        // Getters and Setters
        public Double getTotalRevenue() {
                return totalRevenue;
        }

        public void setTotalRevenue(Double totalRevenue) {
                this.totalRevenue = totalRevenue;
        }

        public Double getRevenueGrowth() {
                return revenueGrowth;
        }

        public void setRevenueGrowth(Double revenueGrowth) {
                this.revenueGrowth = revenueGrowth;
        }

        public Integer getTotalMentors() {
                return totalMentors;
        }

        public void setTotalMentors(Integer totalMentors) {
                this.totalMentors = totalMentors;
        }

        public Integer getMentorGrowth() {
                return mentorGrowth;
        }

        public void setMentorGrowth(Integer mentorGrowth) {
                this.mentorGrowth = mentorGrowth;
        }

        public Integer getTotalStudents() {
                return totalStudents;
        }

        public void setTotalStudents(Integer totalStudents) {
                this.totalStudents = totalStudents;
        }

        public Integer getStudentGrowth() {
                return studentGrowth;
        }

        public void setStudentGrowth(Integer studentGrowth) {
                this.studentGrowth = studentGrowth;
        }

        public Integer getActiveCourses() {
                return activeCourses;
        }

        public void setActiveCourses(Integer activeCourses) {
                this.activeCourses = activeCourses;
        }

        public Double getSatisfactionRate() {
                return satisfactionRate;
        }

        public void setSatisfactionRate(Double satisfactionRate) {
                this.satisfactionRate = satisfactionRate;
        }

        public Double getAvgRevenuePerMentor() {
                return avgRevenuePerMentor;
        }

        public void setAvgRevenuePerMentor(Double avgRevenuePerMentor) {
                this.avgRevenuePerMentor = avgRevenuePerMentor;
        }

        public Double getAvgStudentsPerMentor() {
                return avgStudentsPerMentor;
        }

        public void setAvgStudentsPerMentor(Double avgStudentsPerMentor) {
                this.avgStudentsPerMentor = avgStudentsPerMentor;
        }

        public Double getAvgRating() {
                return avgRating;
        }

        public void setAvgRating(Double avgRating) {
                this.avgRating = avgRating;
        }

        public Double getNetProfit() {
                return netProfit;
        }

        public void setNetProfit(Double netProfit) {
                this.netProfit = netProfit;
        }

        public Double getNetProfitGrowth() {
                return netProfitGrowth;
        }

        public void setNetProfitGrowth(Double netProfitGrowth) {
                this.netProfitGrowth = netProfitGrowth;
        }

        public Double getOperatingCost() {
                return operatingCost;
        }

        public void setOperatingCost(Double operatingCost) {
                this.operatingCost = operatingCost;
        }

        public Double getOperatingCostGrowth() {
                return operatingCostGrowth;
        }

        public void setOperatingCostGrowth(Double operatingCostGrowth) {
                this.operatingCostGrowth = operatingCostGrowth;
        }

        public Double getAvgROI() {
                return avgROI;
        }

        public void setAvgROI(Double avgROI) {
                this.avgROI = avgROI;
        }

        public Double getRoiGrowth() {
                return roiGrowth;
        }

        public void setRoiGrowth(Double roiGrowth) {
                this.roiGrowth = roiGrowth;
        }

        public Double getChurnRate() {
                return churnRate;
        }

        public void setChurnRate(Double churnRate) {
                this.churnRate = churnRate;
        }

        public Double getChurnGrowth() {
                return churnGrowth;
        }

        public void setChurnGrowth(Double churnGrowth) {
                this.churnGrowth = churnGrowth;
        }

        public Double getCac() {
                return cac;
        }

        public void setCac(Double cac) {
                this.cac = cac;
        }

        public Double getCacGrowth() {
                return cacGrowth;
        }

        public void setCacGrowth(Double cacGrowth) {
                this.cacGrowth = cacGrowth;
        }

        public Double getLtv() {
                return ltv;
        }

        public void setLtv(Double ltv) {
                this.ltv = ltv;
        }

        public Double getLtvGrowth() {
                return ltvGrowth;
        }

        public void setLtvGrowth(Double ltvGrowth) {
                this.ltvGrowth = ltvGrowth;
        }

        public List<RevenueChartDTO> getRevenueChart() {
                return revenueChart;
        }

        public void setRevenueChart(List<RevenueChartDTO> revenueChart) {
                this.revenueChart = revenueChart;
        }

        public List<TopMentorDTO> getTopMentors() {
                return topMentors;
        }

        public void setTopMentors(List<TopMentorDTO> topMentors) {
                this.topMentors = topMentors;
        }
}
