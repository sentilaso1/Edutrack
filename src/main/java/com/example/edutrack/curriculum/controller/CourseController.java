package com.example.edutrack.curriculum.controller;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.service.implementation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Controller
public class CourseController {
    private final CourseServiceImpl courseServiceImpl;
    private final MentorCourseServiceImpl mentorCourseServiceImpl;
    private final MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final TagServiceImpl tagServiceImpl;

    public CourseController(CourseServiceImpl courseServiceImpl, CourseTagServiceImpl courseTagServiceImpl, MentorCourseServiceImpl mentorCourseServiceImpl, MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl, TagServiceImpl tagServiceImpl) {
        this.courseServiceImpl = courseServiceImpl;
        this.mentorCourseServiceImpl = mentorCourseServiceImpl;
        this.mentorAvailableTimeServiceImpl = mentorAvailableTimeServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.tagServiceImpl = tagServiceImpl;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        List<Course> courses = courseServiceImpl.findAll();
        model.addAttribute("courses", courses);
        return "course-box";
    }

    @GetMapping("/courses/{courseId}")
    public String courseDetail(@PathVariable("courseId") UUID courseId, Model model) {
        Course course = courseServiceImpl.findById(courseId);
        List<MentorDTO> mentorList = mentorCourseServiceImpl.getMentorsByCourseId(courseId);
        List<TagDTO> tagList = courseTagServiceImpl.findTagsByCourseId(courseId);
        model.addAttribute("mentorList", mentorList);
        model.addAttribute("courseId", courseId);
        model.addAttribute("tagList", tagList);
        model.addAttribute("course", course);
        return "course-detail";
    }

    @GetMapping("/courses/{courseId}/mentor/{mentorId}")
    public String viewMentorAvailability(@PathVariable UUID courseId,
                                         @PathVariable UUID mentorId,
                                         Model model) {
        List<MentorAvailableTimeDTO> times = mentorAvailableTimeServiceImpl.getMentorAvailableTime(mentorId);
        model.addAttribute("availableTimes", times);
        model.addAttribute("courseId", courseId);
        model.addAttribute("mentorId", mentorId);
        return "mentor-availability";
    }

    @GetMapping("/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courseForm", new CourseFormDTO());
        model.addAttribute("tags", tagServiceImpl.findAll());
        return "course-form";
    }




}
