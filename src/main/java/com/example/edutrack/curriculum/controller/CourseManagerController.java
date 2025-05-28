package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.CourseServiceImpl;
import com.example.edutrack.curriculum.service.implementation.CourseTagServiceImpl;
import com.example.edutrack.curriculum.service.implementation.TagServiceImpl;
import com.example.edutrack.curriculum.service.implementation.TeachingMaterialsImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/manager")
@Controller
public class CourseManagerController {
    private final CourseServiceImpl courseService;
    private final MentorServiceImpl mentorService;
    private final MentorServiceImpl mentorServiceImpl;
    private final CourseServiceImpl courseServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final TeachingMaterialsImpl teachingMaterialsImpl;
    private final TagServiceImpl tagServiceImpl;

    public CourseManagerController(CourseServiceImpl courseService, MentorServiceImpl mentorService, MentorServiceImpl mentorServiceImpl, CourseServiceImpl courseServiceImpl, CourseTagServiceImpl courseTagServiceImpl, TeachingMaterialsImpl teachingMaterialsImpl, CourseRepository courseRepository, TagServiceImpl tagServiceImpl) {
        this.courseService = courseService;
        this.mentorService = mentorService;
        this.mentorServiceImpl = mentorServiceImpl;
        this.courseServiceImpl = courseServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.teachingMaterialsImpl = teachingMaterialsImpl;
        this.tagServiceImpl = tagServiceImpl;
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


    @GetMapping("/materials/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int id) {
        TeachingMaterial material = teachingMaterialsImpl.findById(id);
        if (material == null) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(material.getFileType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(material.getName()).build());
        return ResponseEntity.ok().headers(headers).body(material.getFile());
    }

    @GetMapping("/courses/toggle-open/{id}")
    public String toggleOpen(@PathVariable UUID id) {
        Course course = courseServiceImpl.findById(id);
        if (course != null) {
            course.setOpen(!course.getOpen());
            courseServiceImpl.save(course);
        }
        return "redirect:/manager/view";
    }

    @GetMapping("/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courseForm", new CourseFormDTO());
        return "course-form";
    }

    @PostMapping("/courses/create")
    public String createCourse(@ModelAttribute("courseForm") CourseFormDTO courseFormDTO, Model model, RedirectAttributes redirectAttributes) {
        UUID courseID = courseServiceImpl.create(courseFormDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo khoá học thành công");
        redirectAttributes.addFlashAttribute("createdCourseId", courseID);
        return "redirect:/manager/view";
    }

    @GetMapping("/courses/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Course course = courseServiceImpl.findById(id);
        CourseFormDTO form = new CourseFormDTO();
        form.setName(course.getName());
        form.setDescription(course.getDescription());
        List<String> tagTexts = tagServiceImpl.findTagsByCourseId(id).stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());
        form.setTagTexts(tagTexts);
        List<TeachingMaterial> materials = teachingMaterialsImpl.findByCourseId(id);
        model.addAttribute("materials", materials);
        model.addAttribute("courseForm", form);
        model.addAttribute("courseId", id);
        return "manager-course-edit";
    }

    @PostMapping("/courses/edit/{id}")
    public String editCourse(@PathVariable UUID id, @ModelAttribute("courseForm") CourseFormDTO courseFormDTO, Model model, RedirectAttributes redirectAttributes) {
        courseServiceImpl.update(id, courseFormDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khoá học thành công");
        return "redirect:/manager/view";
    }

    @GetMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable int id) {
        UUID courseId = teachingMaterialsImpl.findCourseByMaterialId(id).getId();
        teachingMaterialsImpl.deleteById(id);
        return "redirect:/manager/courses/edit/" + courseId;
    }


}
