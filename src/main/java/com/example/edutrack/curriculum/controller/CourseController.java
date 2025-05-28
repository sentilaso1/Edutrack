package com.example.edutrack.curriculum.controller;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import com.example.edutrack.curriculum.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.curriculum.service.interfaces.TagService;
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


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final CourseTagService courseTagService;
    private final MentorService mentorService;
    private final TagService tagService;

    public CourseController(CourseService courseService, MentorAvailableTimeService availableTimeService, CourseTagService courseTagService, MentorService mentorService, TagService tagService) {
        this.courseService = courseService;
        this.mentorAvailableTimeService = availableTimeService;
        this.courseTagService = courseTagService;
        this.mentorService = mentorService;
        this.tagService = tagService;
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

        Page<Course> coursePage = courseService.findAll(pageable);
        System.out.println("DEBUG: " + coursePage.getContent().get(0));
        model.addAttribute("coursePage", coursePage);
        return "courselist";
    }

    @GetMapping("/courses/{courseId}")
    public String courseDetail(@PathVariable("courseId") UUID courseId, Model model) {
        Course course = courseService.findById(courseId);
        List<Tag> tagList = tagService.findTagsByCourseId(courseId);

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
        Optional<Mentor> mentor =  mentorService.findById(id);
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
        List<MentorAvailableTimeDTO> times = mentorAvailableTimeService.getMentorAvailableTime(mentorId);
        model.addAttribute("availableTimes", times);
        model.addAttribute("courseId", courseId);
        model.addAttribute("mentorId", mentorId);
        return "mentor-availability";
    }

    @GetMapping("/course-mentor/view")
    public String viewMentorAvailableTimes(Model model) {
        List<Course> mentorCourses = mentorService.getCoursesByMentor(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        model.addAttribute("mentorCourses", mentorCourses);
        return "course-mentor-view";

    }













}
