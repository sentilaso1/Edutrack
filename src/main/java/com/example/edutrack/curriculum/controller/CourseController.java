package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.implementation.*;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class CourseController {
    private final CourseServiceImpl courseServiceImpl;
    private final MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final MentorServiceImpl mentorServiceImpl;
    private final TagServiceImpl tagServiceImpl;
    private final CourseMentorService courseMentorService;
    private final CourseMentorServiceImpl courseMentorServiceImpl;

    public CourseController(CourseServiceImpl courseServiceImpl,
                            CourseTagServiceImpl courseTagServiceImpl,
                            MentorAvailableTimeServiceImpl mentorAvailableTimeServiceImpl,
                            MentorServiceImpl mentorServiceImpl,
                            TagServiceImpl tagServiceImpl,
                            CourseMentorService courseMentorService, CourseMentorServiceImpl courseMentorServiceImpl) {
        this.courseServiceImpl = courseServiceImpl;
        this.mentorAvailableTimeServiceImpl = mentorAvailableTimeServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.mentorServiceImpl = mentorServiceImpl;
        this.tagServiceImpl = tagServiceImpl;
        this.courseMentorService = courseMentorService;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
    }

    @GetMapping("/courses")
    public String courses(Model model,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "6") int size_page,
                          @RequestParam(required = false) Integer[] subject,
                          @RequestParam(required = false) String[] skill
                            ) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }
        model.addAttribute("selectedSubjects", subject);
        model.addAttribute("selectedSkills", skill);
        model.addAttribute("pageNumber", page);

        Pageable pageable = PageRequest.of(page - 1, size_page);
        List<Integer> subjectIds = subject != null ? Arrays.asList(subject) : null;
        List<UUID> skillIds = null;
        if (skill != null) {
            skillIds = Arrays.stream(skill)
                    .map(UUID::fromString)
                    .toList();
        }

        Page<CourseMentor> coursePage = courseMentorServiceImpl.findFilteredCourseMentors(
                skillIds,
                subjectIds,
                pageable
        );

        model.addAttribute("subjectList", courseMentorServiceImpl.findAllTags());
        model.addAttribute("skillList", courseMentorServiceImpl.findAllCourses());
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("selectedSkills", skill);
        model.addAttribute("selectedSubjects", subject);
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
