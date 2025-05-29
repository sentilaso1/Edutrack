package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Staff;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
        @Modifying
        @Transactional
        @Query(value = "INSERT INTO staffs (user_id, role) VALUES (:user_id, :role)", nativeQuery = true)
        void insertStaff(@Param("user_id") UUID userId, @Param("role") String role);

        @Modifying
        @Transactional
        @Query(value = "DELETE FROM staffs WHERE user_id = :user_id", nativeQuery = true)
        void deleteById(@Param("user_id") UUID id);

    Optional<Staff> findByEmail(String email);
}
