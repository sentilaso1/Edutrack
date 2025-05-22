package com.example.edutrack.curriculum.service;


import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.model.MentorAvailableTime;
import com.example.edutrack.curriculum.repository.MentorAvailableTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MentorAvailableTimeService {
    private final MentorAvailableTimeRepository mentorAvailableTimeRepository;

    @Autowired
    public MentorAvailableTimeService(MentorAvailableTimeRepository mentorAvailableTimeRepository) {
        this.mentorAvailableTimeRepository = mentorAvailableTimeRepository;
    }
    public List<MentorAvailableTimeDTO> getMentorAvailableTime(UUID mentorId) {
        List<MentorAvailableTime> mentorAvailableTimeList = mentorAvailableTimeRepository.findByMentorId(mentorId);

        return mentorAvailableTimeList.stream().map(time -> new MentorAvailableTimeDTO(time.getDay(), time.getSlot().getStartTime(), time.getSlot().getEndTime())).collect(Collectors.toList());
    }
}
