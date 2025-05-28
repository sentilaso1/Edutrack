package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.TeachingMaterial;
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

    @Override
    public List<TeachingMaterial> findByCourseId(UUID courseId) {
        return teachingMaterialsRepository.findByCourseId(courseId);
    }

    @Override
    public TeachingMaterial findById(int id) {
        return teachingMaterialsRepository.findById(id).orElseThrow(() -> new RuntimeException("File không tồn tại"));
    }

    public void deleteById(int id) {
        teachingMaterialsRepository.deleteById(id);
    }

    public Course findCourseByMaterialId(int id) {
        return teachingMaterialsRepository.findCourseByTeachingMaterialId(id);
    }
}
