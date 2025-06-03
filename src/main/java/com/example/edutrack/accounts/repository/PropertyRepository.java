package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {
        Property findByKey(String key);
}
