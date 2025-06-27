package com.example.edutrack.accounts.dto;

public class ContactFormDTO {
        private String email;
        private String subject;
        private String message;
        private String phone;

        public ContactFormDTO() {
        }

        public ContactFormDTO(String email, String subject, String message, String phone) {
                this.email = email;
                this.subject = subject;
                this.message = message;
                this.phone = phone;
        }
        
        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getSubject() {
                return subject;
        }

        public void setSubject(String subject) {
                this.subject = subject;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public String getPhone() {
                return phone;
        }

        public void setPhone(String phone) {
                this.phone = phone;
        }
}
