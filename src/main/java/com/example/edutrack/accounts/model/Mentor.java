package com.example.edutrack.accounts.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import javax.management.relation.Role;
import java.util.Date;

@Entity
@Table(name = "mentors")
@PrimaryKeyJoinColumn(name = "user_id")
public class Mentor extends User {

    @Column(name = "is_available")
    private Boolean isAvailable = Boolean.FALSE;

    @Column(name = "total_sessions")
    private Integer totalSessions = 0;

    @Column(length = 512)
    private String expertise;

    @Column(columnDefinition = "DECIMAL(2,1)")
    private Double rating;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    public enum Role {
        Admin, Manager
    }

    public Mentor() {
        super();
    }

    public Mentor(User user, Role role) {
        super(user.getEmail(), user.getPassword(), user.getFullName(), user.getPhone());
        this.role = role;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Mentor{" +
                "isAvailable=" + isAvailable +
                ", totalSessions=" + totalSessions +
                ", expertise='" + expertise + '\'' +
                ", rating=" + rating +
                ", role=" + role +
                ", createdDate=" + createdDate +
                '}';
    }
}
