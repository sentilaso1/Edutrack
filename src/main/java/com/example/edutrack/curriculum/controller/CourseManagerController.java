package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.CourseServiceImpl;
import com.example.edutrack.curriculum.service.implementation.CourseTagServiceImpl;
import com.example.edutrack.curriculum.service.implementation.TeachingMaterialsImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/manager")
@Controller
public class CourseManagerController {
    private final CourseServiceImpl courseService;
    private final MentorServiceImpl mentorService;
    private final MentorServiceImpl mentorServiceImpl;
    private final CourseServiceImpl courseServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final TeachingMaterialsImpl teachingMaterialsImpl;
    private final CourseRepository courseRepository;

    public CourseManagerController(CourseServiceImpl courseService, MentorServiceImpl mentorService, MentorServiceImpl mentorServiceImpl, CourseServiceImpl courseServiceImpl, CourseTagServiceImpl courseTagServiceImpl, TeachingMaterialsImpl teachingMaterialsImpl, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.mentorService = mentorService;
        this.mentorServiceImpl = mentorServiceImpl;
        this.courseServiceImpl = courseServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.teachingMaterialsImpl = teachingMaterialsImpl;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/view")
    public String view(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID mentorId,
            @RequestParam(required = false) Boolean open,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false) String sortBy,
            Model model) {

        List<Course> courses = courseService.getFilteredCourses(search, mentorId, open, fromDate, toDate, sortBy);
        List<Mentor> mentors = mentorService.getAllMentors();

        model.addAttribute("courses", courses);
        model.addAttribute("mentors", mentors);
        model.addAttribute("selectedMentorId", mentorId);
        model.addAttribute("selectedOpen", open);
        model.addAttribute("search", search);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sortBy", sortBy);


        return "manager-course-dashboard";
    }

    @GetMapping("/courses/review/{id}")
    public String reviewCourse(@PathVariable UUID id, Model model){
        Mentor mentor = mentorServiceImpl.findByCourseId(id);
        Course course = courseServiceImpl.findById(id);
        List<TagDTO> tagsList = courseTagServiceImpl.findTagsByCourseId(id);
        List<TeachingMaterial> materials = teachingMaterialsImpl.findByCourseId(id);
        model.addAttribute("materials", materials);
        model.addAttribute("tagList", tagsList);
        model.addAttribute("mentor", mentor);
        model.addAttribute("course", course);
        return "course-review";
    }

    @GetMapping("/materials/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int id) {
        TeachingMaterial material = teachingMaterialsImpl.findById(id);
        if (material == null) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(material.getFileType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(material.getName()).build());
        return ResponseEntity.ok().headers(headers).body(material.getFile());
    }

}
