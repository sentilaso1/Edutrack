package com.example.edutrack.accounts.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller(value="mentor")
public class MentorController {
    @GetMapping("/mentor")
    public String mentor(Model model) {
        return "mentor-dashboard";
    }

}
