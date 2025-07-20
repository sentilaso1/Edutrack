package com.example.edutrack.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleStaticResourceNotFound(NoResourceFoundException ex, Model model, HttpServletRequest request) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("statusText", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("stackTrace", sw.toString());

        return "/error/whitelabel";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFound(NoHandlerFoundException ex, Model model, HttpServletRequest request) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("statusText", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("stackTrace", sw.toString());

        return "/error/whitelabel";
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception ex, Model model, HttpServletRequest request) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        Object statusCodeObj = request.getAttribute("jakarta.servlet.error.status_code");
        int statusCode = statusCodeObj != null
                ? (int) statusCodeObj
                : HttpStatus.INTERNAL_SERVER_ERROR.value();

        String statusText = HttpStatus.resolve(statusCode) != null
                ? HttpStatus.valueOf(statusCode).getReasonPhrase()
                : "Unknown Error";

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("statusText", statusText);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("stackTrace", stackTrace);

        return "/error/whitelabel";
    }
}
