package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;

import java.util.List;
import java.util.UUID;

public interface MentorAvailableTimeService {
    List<MentorAvailableTimeDTO> getMentorAvailableTime(UUID mentorId);
}
