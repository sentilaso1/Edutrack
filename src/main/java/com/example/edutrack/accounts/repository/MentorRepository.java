package com.example.edutrack.accounts.repository;

import java.util.UUID;

import com.example.edutrack.accounts.model.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, UUID> {
	
}
