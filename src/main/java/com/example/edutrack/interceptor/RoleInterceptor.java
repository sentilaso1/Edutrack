package com.example.edutrack.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("/unauthorized");
            return false;
        }

        String path = request.getRequestURI();
        String role = session.getAttribute("role").toString();

        if (path.startsWith("/mentee") && !"mentee".equals(role)) {
            response.sendRedirect("/forbidden");
            return false;
        }
        if (path.startsWith("/mentor") && !"mentor".equals(role)) {
            response.sendRedirect("/forbidden");
            return false;
        }
        return true;
    }
}

