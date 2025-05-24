package com.example.edutrack.profiles.controller;

import com.example.edutrack.auth.service.UserService;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CvController {
    private final CvService cvService;
    private final UserService userService;

    @Autowired
    public CvController(CvService cvService, UserService userService) {
        this.cvService = cvService;
        this.userService = userService;
    }

    @GetMapping("/admin/cv/list")
    public String listCVs(@ModelAttribute CVFilterForm params, Model model) {
        List<CV> cvList = new ArrayList<>();
        String filter = params.getFilter();
        String sort = params.getSort();

        if (filter == null || filter.isEmpty()) {
            if (sort == null || sort.equals(CVFilterForm.SORT_DATE_DESC)) {
                cvList = cvService.findAllCVsDateDesc();
            } else {
                cvList = cvService.findAllCVsDateAsc();
            }
        } else {
            if (sort == null || sort.equals(CVFilterForm.SORT_DATE_DESC)) {
                cvList = cvService.findAllCVsByStatusDateDesc(filter);
            } else {
                cvList = cvService.findAllCVsByStatusDateAsc(filter);
            }
        }

        model.addAttribute("cvList", cvList);
        return "/cv/list-cv";
    }
}
