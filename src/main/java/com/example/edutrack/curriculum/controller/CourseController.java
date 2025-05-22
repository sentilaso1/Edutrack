package com.example.edutrack.curriculum.controller;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final MentorCourseService mentorCourseService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final CourseTagService courseTagService;
    private final TagService tagService;

    public CourseController(CourseService courseService, CourseTagService courseTagService , MentorCourseService mentorCourseService, MentorAvailableTimeService mentorAvailableTimeService, TagService tagService) {
        this.courseService = courseService;
        this.mentorCourseService = mentorCourseService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.courseTagService = courseTagService;
        this.tagService = tagService;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);
        return "course-box";
    }

    @GetMapping("/courses/{courseId}")
    public String courseDetail(@PathVariable("courseId") UUID courseId, Model model) {
        Course course = courseService.findById(courseId);
        List<MentorDTO> mentorList = mentorCourseService.getMentorsByCourseId(courseId);
        List<TagDTO> tagList = courseTagService.findTagsByCourseId(courseId);
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
        List<MentorAvailableTimeDTO> times = mentorAvailableTimeService.getMentorAvailableTime(mentorId);
        model.addAttribute("availableTimes", times);
        model.addAttribute("courseId", courseId);
        model.addAttribute("mentorId", mentorId);
        return "mentor-availability";
    }

    @GetMapping("/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courseForm", new CourseFormDTO());
        model.addAttribute("tags", tagService.findAll());
        return "course-form";
    }




}
