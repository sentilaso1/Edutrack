package com.example.edutrack.accounts.service.implementations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.edutrack.curriculum.model.Course;
import org.springframework.stereotype.Service;

import com.example.edutrack.accounts.model.Mentor;

@Service
public class MentorServiceImpl implements com.example.edutrack.accounts.service.interfaces.MentorService {
        private final com.example.edutrack.accounts.repository.MentorRepository mentorRepository;

        public MentorServiceImpl(com.example.edutrack.accounts.repository.MentorRepository mentorRepository) {
                this.mentorRepository = mentorRepository;
        }
        
        public Mentor getMentorById(String id) {
                try {
                        return mentorRepository.findById(UUID.fromString(id))
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Mentor not found with id: " + id));
                } catch (IllegalArgumentException e) {
                        return null;
                }

        }

        public List<Course> getCoursesByMentor(UUID id) {
                return mentorRepository.findCoursesByMentorId(id);
        }

        public List<Mentor> getAllMentors() {
                return mentorRepository.findAll();
        }

        public Mentor findByCourseId(UUID id) {
                return mentorRepository.findByCourses_Id(id);
        }

    public Optional<Mentor> findById(UUID id) {
                return mentorRepository.findById(id);
    }
}
