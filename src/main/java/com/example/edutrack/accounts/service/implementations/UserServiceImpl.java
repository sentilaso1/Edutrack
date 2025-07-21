package com.example.edutrack.accounts.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.edutrack.accounts.dto.UserStatsDTO;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.StaffRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.service.interfaces.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserServiceImpl implements UserService {
        private final UserRepository userRepository;
        private final StaffRepository staffRepository;
        private final MentorRepository mentorRepository;
        private final MenteeRepository menteeRepository;
        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        public UserServiceImpl(UserRepository userRepository, StaffRepository staffRepository,
                        MentorRepository mentorRepository, MenteeRepository menteeRepository) {
                this.userRepository = userRepository;
                this.staffRepository = staffRepository;
                this.mentorRepository = mentorRepository;
                this.menteeRepository = menteeRepository;
        }

        @Override
        public User getUserById(String id) {
                return userRepository.findById(UUID.fromString(id))
                                .orElseThrow(() -> new RuntimeException("User not found"));
        }

        @Override
        public void saveUser(User user) {
                if (user == null) {
                        throw new IllegalArgumentException("User cannot be null");
                }
                userRepository.save(user);
        }

        @Override
        public void lockUser(String id) {
                User user = getUserById(id);
                user.setIsLocked(true);
                userRepository.save(user);
        }

        @Override
        public void unlockUser(String id) {
                User user = getUserById(id);
                user.setIsLocked(false);
                userRepository.save(user);
        }

        @Override
        public void activateUser(String id) {
                User user = getUserById(id);
                user.setIsActive(true);
                userRepository.save(user);
        }

        @Override
        public void deactivateUser(String id) {
                User user = getUserById(id);
                user.setIsActive(false);
                userRepository.save(user);
        }

        // Function 4
        @Override
        @Transactional
        public void grantStaffRole(String userId, Staff.Role role) {
                if (userId == null || userId.trim().isEmpty()) {
                        throw new IllegalArgumentException("User ID cannot be null or empty");
                }
                if (role == null) {
                        throw new IllegalArgumentException("Role cannot be null");
                }
                String trimmedUserId = userId.trim();
                UUID staffId;

                try {
                        staffId = UUID.fromString(trimmedUserId);
                } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid UUID format for user ID: " + trimmedUserId);
                }

                User user;
                try {
                        user = getUserById(trimmedUserId);
                } catch (RuntimeException e) {
                        throw new RuntimeException("User not found with ID: " + trimmedUserId);
                }

                if (!user.getIsActive()) {
                        throw new IllegalStateException("Cannot grant staff role to inactive user");
                }
                if (user.getIsLocked()) {
                        throw new IllegalStateException("Cannot grant staff role to locked user");
                }

                if (staffRepository.existsById(staffId)) {
                        Staff existingStaff = staffRepository.findById(staffId)
                                        .orElseThrow(() -> new IllegalStateException(
                                                        "Staff record not found for existing ID: " + trimmedUserId));
                        if (existingStaff.getRole() == role) {
                                throw new IllegalStateException("User already has the role: " + role.name());
                        }
                        existingStaff.setRole(role);
                        existingStaff.setCreatedDate(new Date());
                        staffRepository.save(existingStaff);
                        return;
                }

                try {
                        if (mentorRepository.existsById(staffId)) {
                                throw new IllegalStateException(
                                                "Cannot grant staff role to user who is already a Mentor");
                        }
                        if (menteeRepository.existsById(staffId)) {
                                throw new IllegalStateException(
                                                "Cannot grant staff role to user who is already a Mentee");
                        }
                        staffRepository.insertStaff(staffId, role.name());

                } catch (IllegalStateException e) {
                        throw e;
                } catch (Exception e) {
                        throw new RuntimeException("Failed to grant staff role: " + e.getMessage(), e);
                }
        }

        // Function 1
        @Override
        public void revokeStaffRole(String id) {
                if (id == null || id.trim().isEmpty()) {
                        throw new IllegalArgumentException("User ID cannot be empty!");
                }

                UUID userId;
                try {
                        userId = UUID.fromString(id);
                } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid user ID format!");
                }

                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                        throw new RuntimeException("User not found!");
                }

                if (!user.getIsActive()) {
                        throw new IllegalStateException("Cannot revoke staff role from inactive user account!");
                }

                Staff staff = staffRepository.findById(userId).orElse(null);
                if (staff == null) {
                        throw new IllegalStateException("User does not have staff role to revoke!");
                }

                if (staff.getRole() == Staff.Role.Admin) {
                        throw new IllegalStateException(
                                        "Cannot revoke admin role! Admin privileges cannot be removed.");
                }

                if (user.getIsLocked()) {
                        throw new IllegalStateException("Cannot revoke staff role from locked user account!");
                }

                staffRepository.deleteById(userId);
        }

        @Override
        public Page<User> searchUsers(String email, String fullName, Boolean isLocked, Boolean isActive,
                        Pageable pageable) {
                return userRepository.searchUsers(email, fullName, isLocked, isActive, pageable);
        }

        @Override
        public Staff getStaffByUserId(String userId) {
                try {
                        Staff staff = staffRepository.findById(UUID.fromString(userId))
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Staff not found for user ID: " + userId));
                        return staff;
                } catch (Exception e) {
                        return null; // Return null if no staff found or any error occurs
                }

        }

        public UserStatsDTO getUserStatistics() {
                long total = userRepository.count();
                long active = userRepository.countByIsActiveTrue();
                long locked = userRepository.countByIsLockedTrue();
                return new UserStatsDTO(total, active, locked);
        }

        public void exportToCsv(Writer writer) {
                List<User> users = userRepository.findAll();
                try (BufferedWriter bw = new BufferedWriter(writer)) {
                        bw.write("ID,Email,Full Name,Is Active,Is Locked\n");
                        for (User user : users) {
                                bw.write(String.format("%s,%s,%s,%b,%b\n",
                                                user.getId(), user.getEmail(), user.getFullName(),
                                                user.getIsActive(), user.getIsLocked()));
                        }
                } catch (IOException e) {
                        throw new RuntimeException("Failed to export users to CSV", e);
                }
        }
}
