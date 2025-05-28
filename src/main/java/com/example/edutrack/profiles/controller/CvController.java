package com.example.edutrack.profiles.controller;

import com.example.edutrack.auth.service.UserService;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class CvController {
    public static final int PAGE_SIZE = 15;

    private final CvService cvService;
    private final UserService userService;

    @Autowired
    public CvController(CvService cvService, UserService userService) {
        this.cvService = cvService;
        this.userService = userService;
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
    public String showCVForm(Model model) {
        model.addAttribute("cv", new CVForm());
        return "cv/create-cv";
    }

    // Handle the form submit
    @PostMapping("cv/create")
    public String handleCVFormSubmission(@ModelAttribute("cv") CVForm request, Model model) {
            CV saved = cvService.createCV(request);
            model.addAttribute("message", "CV created successfully!");
            return "redirect:/cv/mainpage";
    }

    @GetMapping("/cv/edit/{id}")
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

    @PostMapping("/cv/edit/{id}")
    public String handleEditCV(@PathVariable("id") UUID id, @ModelAttribute("cv") CVForm cvForm, RedirectAttributes redirectAttributes) {
        try {
            cvForm.setUserId(id);
            CV updatedCV = cvService.createCV(cvForm);
            redirectAttributes.addFlashAttribute("success", "CV updated successfully.");
            return "redirect:/cv/edit/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating CV: " + e.getMessage());
            return "redirect:/cv/edit/" + id;
        }
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
