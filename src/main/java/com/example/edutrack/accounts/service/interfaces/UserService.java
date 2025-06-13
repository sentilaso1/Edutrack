package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.dto.UserStatsDTO;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.dto.LoginStats;
import com.example.edutrack.accounts.dto.UserStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
        User getUserById(String id);
        void saveUser(User user);
        void lockUser(String id);
        void unlockUser(String id);
        void activateUser(String id);
        void deactivateUser(String id);
        void grantStaffRole(String id, Staff.Role role);
        void revokeStaffRole(String id);
        Staff getStaffByUserId(String userId);
        Page<User> searchUsers(String email, String fullName, Boolean isLocked, Boolean isActive, Pageable pageable);
        UserStatsDTO getUserStatistics();
        UserStats getSummaryStats();
        LoginStats getLoginStats();
}
