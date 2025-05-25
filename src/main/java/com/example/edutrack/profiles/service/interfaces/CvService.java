package com.example.edutrack.profiles.service.interfaces;

import com.example.edutrack.profiles.dto.CVForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.edutrack.profiles.model.CV;

import java.util.List;

public interface CvService {
    List<CV> findAllCVs();
    Page<CV> findAllCVsDateAsc(Pageable pageable);
    Page<CV> findAllCVsDateDesc(Pageable pageable);
    Page<CV> findAllCVsByStatusDateAsc(Pageable pageable, String status);
    Page<CV> findAllCVsByStatusDateDesc(Pageable pageable, String status);
    CV createCV(CVForm form);
}
