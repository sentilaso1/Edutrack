package com.example.edutrack.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/404")
    public String error404() {
        return "/error/404";
    }

    @GetMapping("/unauthorized")
    public String error401() {
        return "/error/401";
    }
}
