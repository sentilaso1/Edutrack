package com.example.edutrack.accounts.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.edutrack.accounts.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
        @Query("SELECT u FROM User u " +
           "WHERE (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "AND (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
           "AND (:isLocked IS NULL OR u.isLocked = :isLocked) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive)")
    List<User> searchUsers(
            @Param("email") String email,
            @Param("fullName") String fullName,
            @Param("isLocked") Boolean isLocked,
            @Param("isActive") Boolean isActive
    );
    
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);

}
