package com.example.edutrack.accounts.service.implementations;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements com.example.edutrack.accounts.service.interfaces.ProfileService {
        private final UserRepository userRepository;

        @Autowired
        public ProfileServiceImpl(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @Override
        public void updateAvatar(String id, MultipartFile file) throws IOException {
                Optional<User> userOpt = userRepository.findById(UUID.fromString(id));
                if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setAvatar(file.getBytes());
                        userRepository.save(user);
                }
        }

        @Override
        public User getUserById(String id) {
                return userRepository.findById(
                                UUID.fromString(id))
                                .orElseThrow(() -> new RuntimeException("User not found"));
        }
}
