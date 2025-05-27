package com.example.edutrack.profiles.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CvServiceImpl implements CvService {
    private final CvRepository cvRepository;
    private final UserRepository userRepository;

    @Autowired
    public CvServiceImpl(CvRepository cvRepository, UserRepository userRepository) {
        this.cvRepository = cvRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CV> findAllCVs() {
        return cvRepository.findAll();
    }

    @Override
    public Page<CV> findAllCVsDateAsc(Pageable pageable) {
        return cvRepository.findAllStatusDateAsc(pageable);
    }

    @Override
    public Page<CV> findAllCVsDateDesc(Pageable pageable) {
        return cvRepository.findAllStatusDateDesc(pageable);
    }

    @Override
    public Page<CV> findAllCVsByStatusDateAsc(Pageable pageable, String status) {
        return cvRepository.findAllByStatusOrderByCreatedDateAsc(pageable, status);
    }

    @Override
    public Page<CV> findAllCVsByStatusDateDesc(Pageable pageable, String status) {
        return cvRepository.findAllByStatusOrderByCreatedDateDesc(pageable, status);
    }

    @Override
    public CV createCV(CVForm cvRequest) {
        User user = userRepository.findById(cvRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CV cv = new CV(
                cvRequest.getSummary(),
                cvRequest.getExperienceYears(),
                cvRequest.getSkills(),
                cvRequest.getEducation(),
                user
        );
        cv.setId(user.getId());
        cv.setExperience(cvRequest.getExperience());
        cv.setCertifications(cvRequest.getCertifications());
        cv.setLanguages(cvRequest.getLanguages());
        cv.setPortfolioUrl(cvRequest.getPortfolioUrl());

        return cvRepository.save(cv);
    }

    @Override
    public CV getCVById(UUID id) {
        return cvRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cv not found with ID: " + id));
    }

    @Override
    public boolean acceptCV(UUID id) {
        Optional<CV> optionalCv = cvRepository.findById(id);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            if (cv.getStatus().equals("pending")) {
                cv.setStatus("approved");
                cvRepository.save(cv);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean rejectCV(UUID id) {
        Optional<CV> optionalCv = cvRepository.findById(id);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            if (cv.getStatus().equals("pending")) {
                cv.setStatus("rejected");
                cvRepository.save(cv);
                return true;
            }
        }
        return false;
    }
}
