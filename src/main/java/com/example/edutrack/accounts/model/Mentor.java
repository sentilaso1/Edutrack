package com.example.edutrack.accounts.model;

import com.example.edutrack.curriculum.model.Course;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import javax.management.relation.Role;
import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "mentor")
    private List<Course> courses;

    public Mentor() {
        super();
    }

    public Mentor(User user) {
        super(user.getEmail(), user.getPassword(), user.getFullName(), user.getPhone(), user.getBirthDate(), user.getGender());
    }

    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
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

    @Override
    public String toString() {
        return "Mentor{" +
                "isAvailable=" + isAvailable +
                ", totalSessions=" + totalSessions +
                ", expertise='" + expertise + '\'' +
                ", rating=" + rating +
                '}';
    }
}
