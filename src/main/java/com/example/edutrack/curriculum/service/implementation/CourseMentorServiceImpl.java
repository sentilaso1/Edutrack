package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseMentorServiceImpl implements CourseMentorService {
     private final CourseMentorRepository courseMentorRepository;
     public CourseMentorServiceImpl(CourseMentorRepository courseMentorRepository) {
         this.courseMentorRepository = courseMentorRepository;
     }

     @Override
     public List<CourseMentor> findByCourseId(UUID courseId) {
         return courseMentorRepository.findByCourseId(courseId);
     }

     @Override
     public void deleteById(UUID applicantId){
         courseMentorRepository.deleteById(applicantId);
     }
}
