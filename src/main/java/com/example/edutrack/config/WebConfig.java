package com.example.edutrack.config;

import com.example.edutrack.accounts.repository.RequestLogRepository;
import com.example.edutrack.interceptor.RequestLoggingInterceptor;
import com.example.edutrack.interceptor.RoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Autowired
        private RequestLogRepository repository;

        @Autowired
        RoleInterceptor roleInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new RequestLoggingInterceptor(repository));
                registry.addInterceptor(roleInterceptor)
                        .addPathPatterns("/admin/**", "/mentor/**", "/staff/**");
        }


}
