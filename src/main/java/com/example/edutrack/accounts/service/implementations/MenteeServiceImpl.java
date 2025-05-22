package com.example.edutrack.accounts.service.implementations;

import org.springframework.stereotype.Service;
import java.util.UUID;
import com.example.edutrack.accounts.model.Mentee;

@Service
public class MenteeServiceImpl implements com.example.edutrack.accounts.service.interfaces.MenteeService {
        private final com.example.edutrack.accounts.repository.MenteeRepository menteeRepository;

        public MenteeServiceImpl(com.example.edutrack.accounts.repository.MenteeRepository menteeRepository) {
            this.menteeRepository = menteeRepository;
        }

        public Mentee getMenteeById(String id) {
               return menteeRepository.findById(UUID.fromString(id))
                       .orElseThrow(() -> new RuntimeException("Mentee not found"));
        }
}
