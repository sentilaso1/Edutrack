package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.model.User;
import java.util.List;

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
        List<User> searchUsers(String email, String fullName, Boolean isLocked, Boolean isActive);
}
