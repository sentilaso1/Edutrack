package com.example.edutrack.profiles.controller;

import com.example.edutrack.accounts.controller.MentorController;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.service.implementations.UserServiceImpl;
import com.example.edutrack.auth.service.UserService;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CVCourseRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.CvServiceImpl;
import com.example.edutrack.profiles.service.interfaces.CvService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class CvController {
    public static final int PAGE_SIZE = 15;

    private final CvService cvService;
    private final CourseService courseService;
    private final CourseMentorRepository courseMentorRepository;
    private final CvRepository cvRepository;
    private final MentorRepository mentorRepository;
    private final MentorController mentorController;



    @Autowired
    public CvController(CvService cvService,
                        CourseService courseService,
                        CourseMentorRepository courseMentorRepository,
                        CvRepository cvRepository,
                        MentorRepository mentorRepository,
                        MentorController mentorController) {
        this.cvService = cvService;
        this.courseService = courseService;
        this.courseMentorRepository = courseMentorRepository;
        this.cvRepository = cvRepository;
        this.mentorRepository = mentorRepository;
        this.mentorController = mentorController;
    }

    @GetMapping("/admin/cv/list/{page}")
    public String listCVs(@ModelAttribute CVFilterForm params, Model model, @PathVariable int page) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }

        String filter = params.getFilter();
        String sort = params.getSort();
        List<String> tags = params.getTags();
        List<String> uniqueSkills = cvService.getAllUniqueSkills();
        boolean running = cvService.isBatchRunning();
        LocalDateTime lastEnd = cvService.getLastBatchEnd();
        long delaySeconds = 60;
        long nextBatchMillis = running
                ? -1 // special flag for UI
                : lastEnd.plusSeconds(delaySeconds).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


        // TODO: Optizimize case when all skills are already selected
        if (tags == null || tags.isEmpty() || tags.contains("all")) {
            tags = uniqueSkills;
        }


        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Page<CV> cvPage = cvService.queryCVs(filter, sort, tags, pageable);

        model.addAttribute("pageNumber", page);
        model.addAttribute("page", cvPage);
        model.addAttribute("skills", uniqueSkills);
        model.addAttribute("nextBatchMillis", nextBatchMillis);
        model.addAttribute("batchRunning", running);

        if (sort != null) {
            model.addAttribute("sort", sort);
        }
        if (filter != null) {
            model.addAttribute("filter", filter);
        }
        model.addAttribute("tags", tags);

        if (cvPage.getTotalPages() > 0 && page > cvPage.getTotalPages()) {
            return "redirect:/404";
        }

        return "/cv/list-cv";
    }

    @GetMapping("/admin/cv/list")
    public String redirectToListCVs() {
        return "redirect:/admin/cv/list/1";
    }

    @GetMapping("mentor/cv/create")
    // Show the CV form
    public String showCVForm(Model model,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "6") int size,
                             HttpSession session) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        UUID userId = user.getId();

        Optional<CV> existingCv = cvRepository.findByUserId(userId);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> coursePage = courseService.findAll(pageable);

        model.addAttribute("cv", new CVForm());
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("pageNumber", page);
        model.addAttribute("userId", userId);

        if (existingCv.isPresent()) {
            CV cv = existingCv.get();
            if (!cv.getStatus().equalsIgnoreCase("rejected")) {
                return "redirect:/mentor/cv/edit/" + userId;
            } else {
                return "cv/create-cv";
            }
        } else {
            return "cv/create-cv";
        }
    }

    // Handle the form submit
    @PostMapping("mentor/cv/create")
    public String handleCVFormSubmission(@ModelAttribute("cv") CVForm request, Model model, HttpSession session) {
        try {
            request.parseSelectedCourses();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to read course details. Please try again.");
            return "redirect:/404";
        }

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        cvService.createCV(request, user.getId());

        model.addAttribute("message", "CV created successfully!");
        return "redirect:/mentor";
    }

    @GetMapping("mentor/cv/edit/{id}")
    public String editCV(@PathVariable("id") UUID id, Model model) {
        CV cv = null;
        try {
            cv = cvService.getCVById(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "CV not found.");
            return "redirect:/404";
        }
        model.addAttribute("cv", cv);
        return "cv/edit-cv";
    }

    @PostMapping("mentor/cv/edit/{id}")
    public String handleEditCV(@PathVariable("id") UUID id, @ModelAttribute("cv") CVForm cvForm, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            cvForm.setUserId(id);
            User user = (User) session.getAttribute("loggedInUser");
            if (user == null) {
                return "redirect:/login";
            }
            cvService.createCV(cvForm, user.getId());
            redirectAttributes.addFlashAttribute("success", "CV updated successfully.");
            return "redirect:/mentor/cv/edit/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating CV: " + e.getMessage());
            return "redirect:/mentor/cv/edit/" + id;
        }
    }

    @GetMapping("/admin/cv/detail/{id}")
    public String detailCV(@PathVariable("id") UUID id, Model model) {
        CV cv = cvService.getCVById(id);
        Optional<Mentor> mentorOpt = mentorRepository.findById(cv.getId());
        if (mentorOpt.isPresent()) {
            List<CourseMentor> registeredCourses = courseMentorRepository.findAllByMentor(mentorOpt.get());
            System.out.println("Courses found: " + registeredCourses.size());
            model.addAttribute("cv", cv);
            model.addAttribute("registeredCourses", registeredCourses);
        }
        return "cv/cv-detail";
    }

    @PostMapping("/cv/accept/{id}")
    public String acceptCv(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        if (cvService.acceptCV(id)) {
            redirectAttributes.addFlashAttribute("success", "CV accepted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "CV must be in 'Pending' status.");
        }
        return "redirect:/admin/cv/list/1";
    }

    @PostMapping("/cv/reject/{id}")
    public String rejectCv(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        if (cvService.rejectCV(id)) {
            redirectAttributes.addFlashAttribute("success", "CV rejected successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "CV must be in 'Pending' status.");
        }
        return "redirect:/admin/cv/list/1";
    }
}
