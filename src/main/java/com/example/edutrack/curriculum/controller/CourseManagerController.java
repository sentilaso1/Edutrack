package com.example.edutrack.curriculum.controller;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.*;
import com.example.edutrack.curriculum.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/manager")
@Controller
public class CourseManagerController {
    private final CourseService courseService;
    private final CourseTagService courseTagService;
    private final TeachingMaterialsService teachingMaterials;
    private final TagService tagService;
    private final CourseMentorService courseMentorService;


    @Autowired
    public CourseManagerController(CourseService courseService, CourseTagService courseTagService, TeachingMaterialsService teachingMaterials, TagService tagService, CourseMentorService courseMentorService) {
        this.courseService = courseService;
        this.courseTagService = courseTagService;
        this.teachingMaterials = teachingMaterials;
        this.tagService = tagService;
        this.courseMentorService = courseMentorService;
    }

    @GetMapping("/view")
    public String view(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String mentorSearch,
            @RequestParam(required = false) String open,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false) String sortBy,
            Model model) {

        if (search != null && search.trim().isEmpty()) search = null;
        if (mentorSearch != null && mentorSearch.trim().isEmpty()) mentorSearch = null;
        if (sortBy != null && sortBy.trim().isEmpty()) sortBy = null;

        Boolean isOpen = null;
        if (open != null && (open.equalsIgnoreCase("true") || open.equalsIgnoreCase("false"))) {
            isOpen = Boolean.parseBoolean(open);
        }

        List<Course> courses = courseService.getFilteredCourses(search, mentorSearch, isOpen, fromDate, toDate, sortBy);

        System.out.println("search=" + search + ", mentorSearch=" + mentorSearch + ", open=" + open + ", fromDate=" + fromDate + ", toDate=" + toDate + ", sortBy=" + sortBy);

        model.addAttribute("courses", courses);
        model.addAttribute("selectedOpen", open);
        model.addAttribute("search", search);
        model.addAttribute("mentorSearch", mentorSearch);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sortBy", sortBy);

        return "manager-course-dashboard";
    }


    @GetMapping("/materials/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int id) {
        TeachingMaterial material = teachingMaterials.findById(id);
        if (material == null) return ResponseEntity.notFound().build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(material.getFileType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(material.getName()).build());
        return ResponseEntity.ok().headers(headers).body(material.getFile());
    }

    @GetMapping("/courses/toggle-open/{id}")
    public String toggleOpen(@PathVariable UUID id) {
        Course course = courseService.findById(id);
        if (course != null) {
            course.setOpen(!course.getOpen());
            courseService.save(course);
        }
        return "redirect:/manager/view";
    }

    @GetMapping("/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courseForm", new CourseFormDTO());
        return "course-form";
    }

    @PostMapping("/courses/create")
    public String createCourse(@ModelAttribute("courseForm") CourseFormDTO courseFormDTO,
                               Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID courseID = courseService.create(courseFormDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo khóa học thành công");
            redirectAttributes.addFlashAttribute("createdCourseId", courseID);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tạo khóa học: " + e.getMessage());
        }
        return "redirect:/manager/courses/create";
    }

    @GetMapping("/courses/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Course course = courseService.findById(id);
        if (course == null) {
            return "redirect:/manager/view";
        }

        CourseFormDTO form = new CourseFormDTO();
        form.setName(course.getName());
        form.setDescription(course.getDescription());
        List<String> tagTexts = tagService.findTagsByCourseId(id).stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());
        form.setTagTexts(tagTexts);
        List<TeachingMaterial> materials = teachingMaterials.findByCourseId(id);

        model.addAttribute("materials", materials);
        model.addAttribute("courseForm", form);
        model.addAttribute("courseId", id);
        return "manager-course-edit";
    }

    @PostMapping("/courses/edit/{id}")
    public String editCourse(@PathVariable UUID id, @ModelAttribute("courseForm") CourseFormDTO courseFormDTO,
                             Model model, RedirectAttributes redirectAttributes) {
        if (courseFormDTO.getFiles() == null) {
            System.out.println("[DEBUG] courseFormDTO.getFiles() = null");
        } else {
            System.out.println("[DEBUG] Number of files received: " + courseFormDTO.getFiles().length);
            for (MultipartFile file : courseFormDTO.getFiles()) {
                System.out.println("[DEBUG] File name: " + file.getOriginalFilename() + ", empty? " + file.isEmpty());
            }
        }
        try {
            courseService.update(id, courseFormDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khóa học thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khóa học: " + e.getMessage());
        }
        return "redirect:/manager/courses/edit/" + id;
    }

    @GetMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            UUID courseId = teachingMaterials.findCourseByMaterialId(id).getId();
            teachingMaterials.deleteById(id);
            return "redirect:/manager/courses/edit/" + courseId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa tài liệu: " + e.getMessage());
            return "redirect:/manager/view";
        }
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourseWithRelatedData(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khóa học thành công");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khóa học: " + e.getMessage());
        }
        return "redirect:/manager/view";
    }


}
