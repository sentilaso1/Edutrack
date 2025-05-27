package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.implementation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class CourseController {
    private final CourseServiceImpl courseServiceImpl;
    private final MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final TagServiceImpl tagServiceImpl;
    private final MentorServiceImpl mentorServiceImpl;
    private final TeachingMaterialsImpl teachingMaterialsImpl;


    public CourseController(CourseServiceImpl courseServiceImpl, CourseTagServiceImpl courseTagServiceImpl, MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl, TagServiceImpl tagServiceImpl, MentorServiceImpl mentorServiceImpl, TeachingMaterialsImpl teachingMaterialsImpl) {
        this.courseServiceImpl = courseServiceImpl;
        this.mentorAvailableTimeServiceImpl = mentorAvailableTimeServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.tagServiceImpl = tagServiceImpl;
        this.mentorServiceImpl = mentorServiceImpl;
        this.teachingMaterialsImpl = teachingMaterialsImpl;
    }

    @GetMapping("/courses")
    public String courses() {
        return "courselist";
    }

    @GetMapping("/courses/{courseId}")
    public String courseDetail(@PathVariable("courseId") UUID courseId, Model model) {
        Course course = courseServiceImpl.findById(courseId);
        List<Tag> tagList = tagServiceImpl.findTagsByCourseId(courseId);

        Mentor mentor = course.getMentor();
        MentorDTO mentorDTO = null;
        if (mentor != null) {
            mentorDTO = new MentorDTO(mentor.getId(), mentor.getFullName(), mentor.getAvatar());
        }

        model.addAttribute("course", course);
        model.addAttribute("tagList", tagList);
        model.addAttribute("mentor", mentorDTO);

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

    @GetMapping("/course-mentor/view")
    public String viewMentorAvailableTimes(Model model) {
        List<Course> mentorCourses = mentorServiceImpl.getCoursesByMentor(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        model.addAttribute("mentorCourses", mentorCourses);
        return "course-mentor-view";

    }













}
