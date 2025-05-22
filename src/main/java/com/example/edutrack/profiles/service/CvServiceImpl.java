package com.example.edutrack.profiles.service;

import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CvServiceImpl implements CvService {
    private final CvRepository cvRepository;

    @Autowired
    public CvServiceImpl(CvRepository cvRepository) {
        this.cvRepository = cvRepository;
    }

    @Override
    public List<CV> findAllCVs() {
        return cvRepository.findAll();
    }

    @Override
    public List<CV> findAllCVsDateAsc() {
        return cvRepository.findAllByOrderByUpdatedDateAsc();
    }

    @Override
    public List<CV> findAllCVsDateDesc() {
        return cvRepository.findAllByOrderByUpdatedDateDesc();
    }
}
