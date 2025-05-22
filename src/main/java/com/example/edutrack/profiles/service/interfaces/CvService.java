package com.example.edutrack.profiles.service.interfaces;

import com.example.edutrack.profiles.model.CV;

import java.util.List;

public interface CvService {
    List<CV> findAllCVs();
    List<CV> findAllCVsDateAsc();
    List<CV> findAllCVsDateDesc();
}
