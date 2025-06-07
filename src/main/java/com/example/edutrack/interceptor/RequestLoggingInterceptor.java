package com.example.edutrack.interceptor;

import java.time.LocalDateTime;
import java.util.Enumeration;
import org.springframework.web.servlet.HandlerInterceptor;
import com.example.edutrack.accounts.model.RequestLog;
import com.example.edutrack.accounts.repository.RequestLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestLoggingInterceptor implements HandlerInterceptor{
        private final RequestLogRepository repository;

    public RequestLoggingInterceptor(RequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.append(name).append("=").append(request.getParameter(name)).append(" ");
        }
        RequestLog log = new RequestLog(
            request.getMethod(),
            request.getRequestURI(),
            request.getRemoteAddr(),
            params.toString().trim(),
            LocalDateTime.now()
        );
        if (request.getMethod().equalsIgnoreCase("POSt")) {
            log.setParameters("Sensitive data not logged for POST requests");
        }

        repository.save(log);
        return true;
    }
}
