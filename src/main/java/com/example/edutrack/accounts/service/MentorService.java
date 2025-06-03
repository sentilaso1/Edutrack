package com.example.edutrack.accounts.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.profiles.model.CV;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;
    private final CourseMentorRepository courseMentorRepository;

    public MentorService(MentorRepository mentorRepository, CourseMentorRepository courseMentorRepository) {
        this.mentorRepository = mentorRepository;
        this.courseMentorRepository = courseMentorRepository;
    }

    // Receiving all mentors
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    // Filter mentors
    public List<Mentor> searchMentors(String name, String[] expertise, Double rating, Integer totalSessions, Boolean isAvailable) {
        List<Mentor> mentors = mentorRepository.searchMentorsBasic(name, rating, totalSessions, isAvailable);

        if (expertise != null && expertise.length > 0) {
            List<String> expertiseList = Arrays.stream(expertise)
                    .map(String::toLowerCase)
                    .toList();

            mentors = mentors.stream()
                    .filter(m -> {
                        String mentorExpertise = m.getExpertise().toLowerCase();
                        return expertiseList.stream().allMatch(mentorExpertise::contains);
                    })
                    .collect(Collectors.toList());
        }

        return mentors;
    }


    public Optional<Mentor> getMentorById(UUID id) {
        return mentorRepository.findById(id);
    }

    public List<Course> getCoursesByMentor(UUID id) {
        return courseMentorRepository.findCoursesByMentorId(id);
    }

    public List<Tag> getTagsByMentor(UUID id) {
        return mentorRepository.findTagsByMentorId(id);
    }

    public Optional<CV> getCVById(UUID id) {
        return mentorRepository.findCVByMentorId(id);
    }
}
