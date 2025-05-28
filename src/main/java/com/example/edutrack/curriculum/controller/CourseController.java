package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.implementation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class CourseController {
    private final CourseServiceImpl courseServiceImpl;
    private final MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final MentorServiceImpl mentorServiceImpl;
    private final TagServiceImpl tagServiceImpl;

    public CourseController(CourseServiceImpl courseServiceImpl,
                            CourseTagServiceImpl courseTagServiceImpl,
                            MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl,
                            MentorServiceImpl mentorServiceImpl, TagServiceImpl tagServiceImpl) {
        this.courseServiceImpl = courseServiceImpl;
        this.mentorAvailableTimeServiceImpl = mentorAvailableTimeServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.mentorServiceImpl = mentorServiceImpl;
        this.tagServiceImpl = tagServiceImpl;
    }

    @GetMapping("/courses")
    public String courses(Model model,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "6") int size
                            ) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }
        model.addAttribute("pageNumber", page);
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Course> coursePage = courseServiceImpl.findAll(pageable);
        System.out.println("DEBUG: " + coursePage.getContent().get(0));
        model.addAttribute("coursePage", coursePage);
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

    @GetMapping("/mentors/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id) {
        Optional<Mentor> mentor =  mentorServiceImpl.findById(id);
        if (mentor.isEmpty() || mentor.get().getAvatar() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");
        return new ResponseEntity<>(mentor.get().getAvatar(), headers, HttpStatus.OK);
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
