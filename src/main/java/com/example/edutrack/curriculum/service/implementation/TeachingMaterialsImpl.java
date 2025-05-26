package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.TeachingMaterials;
import com.example.edutrack.curriculum.repository.TeachingMaterialsRepository;
import com.example.edutrack.curriculum.service.interfaces.TeachingMaterialsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TeachingMaterialsImpl implements TeachingMaterialsService {
    private final TeachingMaterialsRepository teachingMaterialsRepository;
    public TeachingMaterialsImpl(TeachingMaterialsRepository repository) {
        this.teachingMaterialsRepository = repository;
    }

    public List<TeachingMaterials> findByCourseId(UUID courseId) {
        return teachingMaterialsRepository.findByCourseId(courseId);
    }

    public TeachingMaterials findById(int id) {
        return teachingMaterialsRepository.findById(id).orElseThrow(() -> new RuntimeException("File không tồn tại"));
    }
}
