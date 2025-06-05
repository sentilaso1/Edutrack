package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;

import java.util.List;

public interface MenteeService {
    public Mentee getMenteeById(String id);
    public long countAll();
    List<Mentee> findAll();
}
