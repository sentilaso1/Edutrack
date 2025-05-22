package com.example.edutrack.accounts.service.interfaces;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;
import com.example.edutrack.accounts.model.User;

public interface ProfileService {
        
        public void updateAvatar(String id, MultipartFile file) throws IOException;
        public User getUserById(String id) throws IOException;
}
