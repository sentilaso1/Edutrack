package com.example.edutrack.accounts.service.implementations;

import java.util.*;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.edutrack.accounts.model.Mentor;

@Service
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MentorServiceImpl(MentorRepository mentorRepository, MenteeRepository menteeRepository) {
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
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


    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }


    public Optional<Mentor> findById(UUID id) {
        return mentorRepository.findById(id);
    }

    @Override
    public long countAll() {
        return mentorRepository.count();
    }

    @Override
    public List<Mentor> findAll() {
        return mentorRepository.findAll();
    }

    @Override
    public List<Mentor> getTopMentorsByRatingOrSessions(int limit) {
        return mentorRepository.findTopActiveMentors(PageRequest.of(0, limit));
    }

    @Override
    public List<Mentor> findMentorsByMenteeInterest(UUID menteeId, int limit){
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("Mentee not found"));
        List<String> interests = Arrays.stream(mentee.getInterests().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        List<Mentor> mentorList = new ArrayList<>();
        Set<UUID> seen = new HashSet<>();

        for(String keyword : interests){
            List<Mentor> mentorWithKeyword = mentorRepository.findByExpertiseKeyword(keyword, PageRequest.of(0, limit));
            for (Mentor mentor : mentorWithKeyword){
                if(seen.add(mentor.getId())){
                    mentorList.add(mentor);
                    if (mentorList.size() >= limit) return mentorList;
                }
            }

        }
        if (mentorList.size() < limit) {
            int remaining = limit - mentorList.size();
            List<Mentor> topMentors = mentorRepository.findTopActiveMentors(PageRequest.of(0, remaining));
            for (Mentor mentor : topMentors) {
                if (seen.add(mentor.getId())) {
                    mentorList.add(mentor);
                    if (mentorList.size() >= limit) {
                        break;
                    }
                }
            }
        }
        return mentorList;

    }

    @Transactional
    @Override
    public Optional<Mentor> promoteToMentor(UUID userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            return Optional.empty();
        }

        Mentor mentor = new Mentor();
        mentor.setTotalSessions(0);
        mentor.setAvailable(false);
        mentor.setExpertise(null);
        mentor.setRating(null);

        mentor.setId(userId);
        return Optional.ofNullable(entityManager.merge(mentor));
    }
}
