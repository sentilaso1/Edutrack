package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Staff;

import java.util.Optional;

public interface StaffService {
    Optional<Staff> findByEmail(String email);
}
