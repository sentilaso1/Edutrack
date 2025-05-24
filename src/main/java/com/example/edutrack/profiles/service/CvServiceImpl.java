package com.example.edutrack.profiles.service;

import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<CV> list = new ArrayList<>();

        list.addAll(cvRepository.findAllByStatusOrderByUpdatedDateDesc(CV.STATUS_PENDING));
        list.addAll(cvRepository.findAllByStatusOrderByUpdatedDateDesc(CV.STATUS_APPROVED));
        list.addAll(cvRepository.findAllByStatusOrderByUpdatedDateDesc(CV.STATUS_REJECTED));

        return list;
    }

    @Override
    public List<CV> findAllCVsByStatusDateAsc(String status) {
        return cvRepository.findAllByStatusOrderByUpdatedDateAsc(status);
    }

    @Override
    public List<CV> findAllCVsByStatusDateDesc(String status) {
        return cvRepository.findAllByStatusOrderByUpdatedDateDesc(status);
    }
}
