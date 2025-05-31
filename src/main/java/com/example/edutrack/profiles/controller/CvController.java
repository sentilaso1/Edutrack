package com.example.edutrack.profiles.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.implementations.UserServiceImpl;
import com.example.edutrack.auth.service.UserService;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class CvController {
    public static final int PAGE_SIZE = 15;

    private final CvService cvService;
    private final UserService userService;
    private final CourseService courseService;

    @Autowired
    public CvController(CvService cvService, UserService userService, CourseService courseService) {
        this.cvService = cvService;
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/admin/cv/list/{page}")
    public String listCVs(@ModelAttribute CVFilterForm params, Model model, @PathVariable int page) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }

        model.addAttribute("pageNumber", page);

        Page<CV> cvPage = null;
        String filter = params.getFilter();
        String sort = params.getSort();
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        if (filter == null || filter.isEmpty()) {
            if (sort == null || sort.equals(CVFilterForm.SORT_DATE_DESC)) {
                cvPage = cvService.findAllCVsDateDesc(pageable);
            } else {
                cvPage = cvService.findAllCVsDateAsc(pageable);
            }
        } else {
            if (sort == null || sort.equals(CVFilterForm.SORT_DATE_DESC)) {
                cvPage = cvService.findAllCVsByStatusDateDesc(pageable, filter);
            } else {
                cvPage = cvService.findAllCVsByStatusDateAsc(pageable, filter);
            }
        }

        model.addAttribute("page", cvPage);
        return "/cv/list-cv";
    }

    @GetMapping("/admin/cv/list")
    public String redirectToListCVs() {
        return "redirect:/admin/cv/list/1";
    }

    @GetMapping("cv/create")
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

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> coursePage = courseService.findAll(pageable);

        model.addAttribute("cv", new CVForm());
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("pageNumber", page);
        model.addAttribute("userId", userId);
        return "cv/create-cv";
    }

    // Handle the form submit
    @PostMapping("cv/create")
    public String handleCVFormSubmission(@ModelAttribute("cv") CVForm request, Model model) {
            CV saved = cvService.createCV(request);
            model.addAttribute("message", "CV created successfully!");
            return "redirect:/home"; // Landing page change later
    }

    @GetMapping("/admin/cv/detail/{id}")
    public String detailCV(@PathVariable("id") UUID id, Model model) {
        CV cv = cvService.getCVById(id);
        model.addAttribute("cv", cv);
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
