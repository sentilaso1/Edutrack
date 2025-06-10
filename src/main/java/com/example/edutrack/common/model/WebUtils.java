package com.example.edutrack.common.model;

import jakarta.servlet.http.HttpServletRequest;

public final class WebUtils {
    public static String getRealClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Chuyển IPv6 localhost (::1) thành IPv4 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        // Nếu header X-Forwarded-For có nhiều IP (ngăn cách bởi dấu phẩy)
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
