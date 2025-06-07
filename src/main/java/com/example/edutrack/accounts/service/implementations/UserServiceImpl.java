package com.example.edutrack.accounts.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.edutrack.accounts.dto.UserStatsDTO;
import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
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
import java.util.List;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserServiceImpl implements UserService{
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

        @Override
        @Transactional
        public void grantStaffRole(String userId, Staff.Role role) {
                UUID staffId = UUID.fromString(userId);
                User user = getUserById(userId);
                if (staffRepository.existsById(staffId)) {
                        Staff staff = staffRepository.findById(staffId)
                                        .orElseThrow(() -> new IllegalStateException(
                                                        "Staff record not found for existing ID: " + userId));
                        staff.setRole(role);
                        staff.setCreatedDate(new Date());
                        staffRepository.save(staff);
                        return;
                }
                try {
                        if (mentorRepository.existsById(staffId) || menteeRepository.existsById(staffId)) {
                                throw new IllegalStateException(
                                                "Không thể gán vai trò nhân viên cho người dùng đã là Mentor hoặc Mentee");
                                
                        }
                        staffRepository.insertStaff(staffId, role.name());
                } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                }
        }

        @Override
        public void revokeStaffRole(String id) {
                staffRepository.deleteById(UUID.fromString(id));
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
}
