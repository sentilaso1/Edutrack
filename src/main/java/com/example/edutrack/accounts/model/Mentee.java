package com.example.edutrack.accounts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "mentees")
@PrimaryKeyJoinColumn(name = "user_id")
public class Mentee extends User  {

    @Column(name = "total_sessions")
    private Integer totalSessions;

    @Column(length = 512)
    private String interests;

    public Mentee() {

    }

    public Mentee(User user) {
        super(user);
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public String getInterests() {
        return interests;
    }
    public void setInterests(String interests) {
        this.interests = interests;
    }
}
