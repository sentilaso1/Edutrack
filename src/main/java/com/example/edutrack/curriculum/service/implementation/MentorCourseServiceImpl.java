package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.MentorCourseRepository;
import com.example.edutrack.curriculum.service.interfaces.MentorCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MentorCourseServiceImpl implements MentorCourseService {
    private final MentorCourseRepository mentorCourseRepository;

    @Autowired
    public MentorCourseServiceImpl(MentorCourseRepository mentorCourseRepository) {
        this.mentorCourseRepository = mentorCourseRepository;
    }

    public List<MentorDTO> getMentorsByCourseId(UUID courseId) {
        List<CourseMentor> mentorListInCourse = mentorCourseRepository.findByCourseId(courseId);
        return mentorListInCourse.stream().map(mentor -> new MentorDTO(mentor.getId().getMentorId(), mentor.getMentor().getFullName(), mentor.getMentor().getAvatar())).collect(Collectors.toList());
    }
}
