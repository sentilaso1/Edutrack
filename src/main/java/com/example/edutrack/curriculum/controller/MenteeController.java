package com.example.edutrack.curriculum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenteeController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "mentee/mentee-dashboard";
    }
}
