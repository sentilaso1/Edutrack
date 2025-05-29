package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.*;
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
    private final CourseServiceImpl courseServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final TeachingMaterialsImpl teachingMaterialsImpl;
    private final TagServiceImpl tagServiceImpl;
    private final CourseMentorServiceImpl courseMentorServiceImpl;


    @Autowired
    public CourseManagerController(CourseServiceImpl courseServiceImpl, CourseTagServiceImpl courseTagServiceImpl, TeachingMaterialsImpl teachingMaterialsImpl, TagServiceImpl tagServiceImpl, CourseMentorServiceImpl courseMentorServiceImpl) {
        this.courseServiceImpl = courseServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.teachingMaterialsImpl = teachingMaterialsImpl;
        this.tagServiceImpl = tagServiceImpl;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
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

        List<Course> courses = courseServiceImpl.getFilteredCourses(search, mentorSearch, isOpen, fromDate, toDate, sortBy);

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
    public String createCourse(@ModelAttribute("courseForm") CourseFormDTO courseFormDTO,
                               Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID courseID = courseServiceImpl.create(courseFormDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo khóa học thành công");
            redirectAttributes.addFlashAttribute("createdCourseId", courseID);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tạo khóa học: " + e.getMessage());
        }
        return "redirect:/manager/courses/create";
    }

    @GetMapping("/courses/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Course course = courseServiceImpl.findById(id);
        if (course == null) {
            return "redirect:/manager/view";
        }

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
            courseServiceImpl.update(id, courseFormDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khóa học thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật khóa học: " + e.getMessage());
        }
        return "redirect:/manager/courses/edit/" + id;
    }

    @GetMapping("/materials/delete/{id}")
    public String deleteMaterial(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            UUID courseId = teachingMaterialsImpl.findCourseByMaterialId(id).getId();
            teachingMaterialsImpl.deleteById(id);
            return "redirect:/manager/courses/edit/" + courseId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa tài liệu: " + e.getMessage());
            return "redirect:/manager/view";
        }
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            courseServiceImpl.deleteCourseWithRelatedData(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khóa học thành công");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa khóa học: " + e.getMessage());
        }
        return "redirect:/manager/view";
    }


}
