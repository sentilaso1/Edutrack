package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
        Optional<Property> findByKey(String key);
}
