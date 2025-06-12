package com.example.edutrack.common.controller;

import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchStatusController {

    @Autowired
    private CvService cvService;

    @GetMapping("/status")
    public Map<String, Object> getBatchStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", cvService.isBatchRunning());
        status.put("lastEnd", cvService.getLastBatchEnd());
        return status;
    }
}