package com.example.edutrack.profiles.repository;

import com.example.edutrack.profiles.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CvRepository extends JpaRepository<CV, UUID> {
    List<CV> findAllByOrderByUpdatedDateDesc();
    List<CV> findAllByOrderByUpdatedDateAsc();
    List<CV> findAllByStatusOrderByUpdatedDateDesc(String status);
    List<CV> findAllByStatusOrderByUpdatedDateAsc(String status);
}
