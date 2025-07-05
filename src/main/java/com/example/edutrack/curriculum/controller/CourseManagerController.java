package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.*;
import com.example.edutrack.curriculum.service.interfaces.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
    public CourseManagerController(CourseService courseService, CourseTagService courseTagService,  TeachingMaterialsService teachingMaterials, TagService tagService, CourseMentorService courseMentorService) {
        this.courseService = courseService;
        this.courseTagService = courseTagService;
        this.teachingMaterials = teachingMaterials;
        this.tagService = tagService;
        this.courseMentorService = courseMentorService;
    }

    @GetMapping("/course-dashboard")
    public String view(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String mentorSearch,
            @RequestParam(required = false) String open,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        System.out.println("=== Debug /view ===");
        System.out.println("Received params:");
        System.out.println("search = " + search);
        System.out.println("mentorSearch = " + mentorSearch);
        System.out.println("open = " + open);
        System.out.println("fromDate = " + fromDate);
        System.out.println("toDate = " + toDate);
        System.out.println("sortBy = " + sortBy);
        System.out.println("page = " + page);

        if (search != null && search.trim().isEmpty()) search = null;
        if (mentorSearch != null && mentorSearch.trim().isEmpty()) mentorSearch = null;
        if (sortBy != null && sortBy.trim().isEmpty()) sortBy = null;

        Boolean isOpen = null;
        if (open != null && (open.equalsIgnoreCase("true") || open.equalsIgnoreCase("false"))) {
            isOpen = Boolean.parseBoolean(open);
        }
        System.out.println("Parsed isOpen = " + isOpen);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Course> coursePage = courseService.getFilteredCourses(search, mentorSearch, isOpen, fromDate, toDate, sortBy, pageable);
        System.out.println("coursePage.getTotalElements() = " + coursePage.getTotalElements());
        System.out.println("coursePage.getTotalPages() = " + coursePage.getTotalPages());

        Map<UUID, List<Mentor>> acceptedMentorMap = courseService.getAcceptedMentorsForCourses(coursePage.getContent());
        Map<UUID, Integer> pendingMentorMap = courseService.getPendingApplicantCountForCourses(coursePage.getContent());

        System.out.println("acceptedMentorMap size = " + acceptedMentorMap.size());
        System.out.println("pendingMentorMap size = " + pendingMentorMap.size());

        System.out.println("Courses in page:");
        for (Course course : coursePage.getContent()) {
            System.out.println("Course ID: " + course.getId() + ", Name: " + course.getName() + ", Open: " + course.getOpen());
        }

        model.addAttribute("pendingApplicantCount", pendingMentorMap);
        model.addAttribute("acceptedMentorMap", acceptedMentorMap);
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("selectedOpen", open);
        model.addAttribute("search", search);
        model.addAttribute("mentorSearch", mentorSearch);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());

        System.out.println("=== End Debug /view ===");

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
    public String createCourse(@Valid @ModelAttribute("courseForm") CourseFormDTO courseFormDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        boolean hasAtLeastOneFile = false;
        if(courseFormDTO.getFiles() != null) {
            for (MultipartFile file : courseFormDTO.getFiles()) {
                if (file != null && !file.isEmpty()) {
                    hasAtLeastOneFile = true;
                    break;
                }
            }
        }
        if (!hasAtLeastOneFile) {
            bindingResult.rejectValue("files", "files.empty", "Phải upload ít nhất 1 tài liệu");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("courseForm", courseFormDTO);
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "course-form";
        }

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
    public String editCourse(@PathVariable UUID id,
                             @Valid @ModelAttribute("courseForm") CourseFormDTO courseFormDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        List<TeachingMaterial> materials = teachingMaterials.findByCourseId(id);
        int currentFileCount = materials != null ? materials.size() : 0;

        int newFileCount = 0;
        if (courseFormDTO.getFiles() != null) {
            for (MultipartFile file : courseFormDTO.getFiles()) {
                if (file != null && !file.isEmpty()) {
                    newFileCount++;
                }
            }
        }

        int totalFileCount = currentFileCount + newFileCount;
        System.out.println("[DEBUG] Current files: " + currentFileCount);
        System.out.println("[DEBUG] New files: " + newFileCount);
        System.out.println("[DEBUG] Total files: " + totalFileCount);

        String fileError = null;
        if (totalFileCount > 5) {
            fileError = "Vượt quá giới hạn! Hiện tại: " + currentFileCount +
                    " file, thêm mới: " + newFileCount + " file. Tối đa 5 file.";
        } else if (totalFileCount < 1) {
            fileError = "Khóa học phải có ít nhất 1 tài liệu!";
        }

        if (fileError != null || bindingResult.hasErrors()) {
            Course course = courseService.findById(id);

            model.addAttribute("materials", materials);
            model.addAttribute("courseForm", courseFormDTO);
            model.addAttribute("courseId", id);
            model.addAttribute("fileError", fileError);

            if (bindingResult.hasErrors()) {
                model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đã nhập!");
            }

            return "manager-course-edit";
        }

        try {
            courseService.update(id, courseFormDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khóa học thành công!");
            redirectAttributes.addFlashAttribute("courseId", id);
        } catch (Exception e) {
            System.out.println("[ERROR] Exception when updating course: " + e.getMessage());
            e.printStackTrace();
            Course course = courseService.findById(id);

            model.addAttribute("materials", materials);
            model.addAttribute("courseForm", courseFormDTO);
            model.addAttribute("courseId", id);
            model.addAttribute("errorMessage", "Lỗi khi cập nhật khóa học: " + e.getMessage());
            return "manager-course-edit";
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
