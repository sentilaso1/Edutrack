package com.example.edutrack.common.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class EndpointRegistry {

    private final List<String> getEndpoints;

    public EndpointRegistry(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        this.getEndpoints = mapping.getHandlerMethods().entrySet().stream()
                .filter(entry -> {
                    RequestMappingInfo info = entry.getKey();
                    Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
                    return methods.contains(RequestMethod.GET) || methods.isEmpty();
                })
                .flatMap(entry -> {
                    RequestMappingInfo info = entry.getKey();
                    if (info.getPathPatternsCondition() != null) {
                        return info.getPathPatternsCondition().getPatterns().stream()
                                .map(Object::toString);
                    } else {
                        return Stream.empty();
                    }
                })
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getGetEndpoints() {
        return getEndpoints;
    }
}
