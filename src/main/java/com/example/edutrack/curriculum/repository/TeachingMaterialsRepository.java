package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.TeachingMaterial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingMaterialsRepository extends CrudRepository<TeachingMaterial, Integer> {

}
