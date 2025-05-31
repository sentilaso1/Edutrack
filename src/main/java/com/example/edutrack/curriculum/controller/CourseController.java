package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.MentorAvailableTimeDTO;
import com.example.edutrack.curriculum.dto.MentorDTO;
import com.example.edutrack.curriculum.model.ApplicationStatus;
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
import java.util.stream.Collectors;

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
                          @RequestParam(required = false) List<Integer> subject,
                          @RequestParam(required = false) List<UUID> skill,
                          @RequestParam(required = false) String order_by) {

        if (page < 1) {
            return "redirect:/404";
        }

        Pageable pageable = PageRequest.of(page - 1, size_page);
        Page<CourseMentor> coursePage;

        List<Integer> subjectIds;
        if(subject != null && !subject.isEmpty()) {
            subjectIds = subject;
        }else{
            subjectIds = null;
        }
        List<UUID> skillIds;  if(skill != null && !skill.isEmpty()){
            skillIds = skill;
        }else{
            skillIds = null;
        }

        if ("newest".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByCreatedDateDesc(pageable);
        } else if ("oldest".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByCreatedDateAsc(pageable);
        } else if ("title_asc".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByTitleAsc(pageable);
        } else if ("title_desc".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByTitleDesc(pageable);
        } else {
            coursePage = courseMentorServiceImpl.findFilteredCourseMentors(skillIds, subjectIds, pageable);
        }

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("page", page);
        model.addAttribute("subjectList", courseMentorServiceImpl.findAllTags());
        model.addAttribute("skillList", courseMentorServiceImpl.findAllCourses());
        model.addAttribute("selectedSubjects", subject);
        model.addAttribute("selectedSkills", skill);

        return "courselist";
    }


    @GetMapping("/courses/{courseId}")
    public String courseDetail(@PathVariable("courseId") UUID courseId, Model model) {
        Course course = courseServiceImpl.findById(courseId);
        List<Tag> tagList = tagServiceImpl.findTagsByCourseId(courseId);

        List<MentorDTO> mentors = course.getApplications().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.ACCEPTED)
                .map(app -> {
                    Mentor m = app.getMentor();
                    return new MentorDTO(m.getId(), m.getFullName(), m.getAvatar());
                })
                .collect(Collectors.toList());

        model.addAttribute("course", course);
        model.addAttribute("tagList", tagList);
        model.addAttribute("mentors", mentors);

        return "course-detail";
    }


    @GetMapping("/mentors/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id) {
        Optional<Mentor> mentor = mentorServiceImpl.findById(id);
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



}
