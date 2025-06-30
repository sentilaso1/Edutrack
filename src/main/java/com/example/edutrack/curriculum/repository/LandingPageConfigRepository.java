package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.LandingPageConfig;
import com.example.edutrack.curriculum.model.MenteeLandingRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LandingPageConfigRepository extends JpaRepository<LandingPageConfig, Long> {
    Optional<LandingPageConfig> findByRole(MenteeLandingRole role);
}
