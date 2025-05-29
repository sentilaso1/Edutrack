package com.example.edutrack.accounts.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.profiles.model.CV;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;

    public MentorService(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    // Receiving all mentors
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    // Filter mentors
    public List<Mentor> searchMentors(String name, String expertise, Double rating, Integer totalSessions, Boolean isAvailable) {
        return mentorRepository.searchMentors(name, expertise, rating, totalSessions, isAvailable);
    }

    public Optional<Mentor> getMentorById(UUID id) {
        return mentorRepository.findById(id);
    }

    public List<Course> getCoursesByMentor(UUID id) {
        return mentorRepository.findCoursesByMentorId(id);
    }

    public List<Tag> getTagsByMentor(UUID id) {
        return mentorRepository.findTagsByMentorId(id);
    }

    public Optional<CV> getCVById(UUID id) {
        return mentorRepository.findCVByMentorId(id);
    }
}
