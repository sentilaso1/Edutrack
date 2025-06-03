package com.example.edutrack.accounts.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RequestLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String method;
        private String uri;
        private String ip;
        private String parameters;
        private LocalDateTime timestamp;

        public RequestLog() {
        }

        public RequestLog(String method, String uri, String ip, String parameters, LocalDateTime timestamp) {
                this.method = method;
                this.uri = uri;
                this.ip = ip;
                this.parameters = parameters;
                this.timestamp = timestamp;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getMethod() {
                return method;
        }

        public void setMethod(String method) {
                this.method = method;
        }

        public String getUri() {
                return uri;
        }

        public void setUri(String uri) {
                this.uri = uri;
        }

        public String getIp() {
                return ip;
        }

        public void setIp(String ip) {
                this.ip = ip;
        }

        public String getParameters() {
                return parameters;
        }

        public void setParameters(String parameters) {
                this.parameters = parameters;
        }

        public LocalDateTime getTimestamp() {
                return timestamp;
        }
        public void setTimestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
        }
}
