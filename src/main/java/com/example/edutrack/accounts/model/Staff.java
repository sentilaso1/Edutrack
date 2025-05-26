package com.example.edutrack.accounts.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "staffs")
@PrimaryKeyJoinColumn(name = "user_id")
public class Staff extends User {
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    public enum Role {
        Admin, Manager
    }

    public Staff() {
        super();
    }

    public Staff(UUID id ,Role role) {
        this.setId(id);
        this.role = role;
        this.createdDate = new Date();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "role=" + role +
                ", createdDate=" + createdDate +
                '}';
    }
}
