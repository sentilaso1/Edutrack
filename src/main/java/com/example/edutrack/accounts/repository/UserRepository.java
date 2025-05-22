package com.example.edutrack.accounts.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.edutrack.accounts.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {


}
