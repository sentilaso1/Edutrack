package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.edutrack.accounts.model.Mentee;

@Service
public class MenteeServiceImpl implements MenteeService {
    private final MenteeRepository menteeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MenteeServiceImpl(MenteeRepository menteeRepository) {
        this.menteeRepository = menteeRepository;
    }

    public Mentee getMenteeById(String id) {
        return menteeRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Mentee not found"));
    }

    @Override
    public long countAll() {
        return menteeRepository.count();
    }

    @Override
    public List<Mentee> findAll() {
        return menteeRepository.findAll();
    }

    @Override
    public Optional<Mentee> findById(UUID id) {
        return menteeRepository.findById(id);
    }

    @Transactional
    @Override
    public Optional<Mentee> promoteToMentee(UUID userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            return Optional.empty();
        }

        Mentee mentee = new Mentee();
        mentee.setTotalSessions(0);
        mentee.setInterests(null);

        mentee.setId(user.getId());
        return Optional.ofNullable(entityManager.merge(mentee));
    }

    @Override
    public Optional<Mentee> findByEmail(String email) {
        return menteeRepository.findByEmail(email);
    }
}
