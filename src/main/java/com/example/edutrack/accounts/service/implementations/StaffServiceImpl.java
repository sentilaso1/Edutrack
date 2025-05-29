package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.repository.StaffRepository;
import com.example.edutrack.accounts.service.interfaces.StaffService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    @Override
    public Optional<Staff> findByEmail(String email) {
        return staffRepository.findByEmail(email);
    }
}
