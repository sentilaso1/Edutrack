package com.example.edutrack.profiles.controller;

import com.example.edutrack.auth.service.UserService;
import com.example.edutrack.profiles.dto.CVFilterForm;
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

import java.util.ArrayList;
import java.util.List;

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
}
