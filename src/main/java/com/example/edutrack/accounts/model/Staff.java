package com.example.edutrack.accounts.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

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

    public Staff(User user, Role role) {
        super(user.getEmail(), user.getPassword(), user.getFullName(), user.getPhone());
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
