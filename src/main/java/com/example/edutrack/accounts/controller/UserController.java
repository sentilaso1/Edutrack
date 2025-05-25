package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.service.interfaces.UserService;
import com.example.edutrack.accounts.dto.UserFilter;
import com.example.edutrack.accounts.dto.UserWithRoleDTO;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/users")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @GetMapping
        public String showUserManagement(Model model,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) String fullName,
                        @RequestParam(required = false) Boolean isLocked,
                        @RequestParam(required = false) Boolean isActive) {
                List<User> users = userService.searchUsers(email, fullName, isLocked, isActive);
                List<UserWithRoleDTO> userDtos = new ArrayList<>();

                for (User user : users) {
                        String role = "USER";
                        if (user.getId() != null) {
                                Staff staff = userService.getStaffByUserId(user.getId().toString());
                                if (staff != null) {
                                        role = staff.getRole().toString();
                                }
                        }
                        userDtos.add(new UserWithRoleDTO(
                                        user.getId(),
                                        user.getEmail(),
                                        user.getFullName(),
                                        role,
                                        user.getIsLocked(),
                                        user.getIsActive()));
                }

                model.addAttribute("users", userDtos);
                model.addAttribute("filters", new UserFilter(email, fullName, isLocked, isActive));
                return "accounts/html/user-management";
        }

        @PostMapping("/{id}/lock")
        public String toggleLock(@PathVariable String id, RedirectAttributes redirectAttributes) {
                try {
                        User user = userService.getUserById(id);
                        if (user == null) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                                return "redirect:/admin/users";
                        }
                        user.setIsLocked(!user.getIsLocked()); // Toggle lock state
                        userService.saveUser(user);
                        redirectAttributes.addFlashAttribute("successMessage",
                                        user.getIsLocked() ? "Khóa người dùng thành công!"
                                                        : "Mở khóa người dùng thành công!");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Lỗi khi thay đổi trạng thái khóa: " + e.getMessage());
                }
                return "redirect:/admin/users";
        }

        @PostMapping("/{id}/activate")
        public String toggleActivate(@PathVariable String id, RedirectAttributes redirectAttributes) {
                try {
                        User user = userService.getUserById(id);
                        if (user == null) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                                return "redirect:/admin/users";
                        }
                        user.setIsActive(!user.getIsActive()); // Toggle active state
                        userService.saveUser(user);
                        redirectAttributes.addFlashAttribute("successMessage",
                                        user.getIsActive() ? "Kích hoạt người dùng thành công!"
                                                        : "Hủy kích hoạt người dùng thành công!");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Lỗi khi thay đổi trạng thái kích hoạt: " + e.getMessage());
                }
                return "redirect:/admin/users";
        }

        @PostMapping("/{id}/grant-staff")
        public String grantStaff(@PathVariable String id, @RequestParam String role,
                        RedirectAttributes redirectAttributes) {
                try {
                        if (role == null || role.isEmpty()) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn vai trò!");
                                return "redirect:/admin/users";
                        }
                        Staff.Role staffRole;
                        try {
                                staffRole = Staff.Role.valueOf(role);
                        } catch (IllegalArgumentException e) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Vai trò không hợp lệ!");
                                return "redirect:/admin/users";
                        }
                        userService.grantStaffRole(id, Staff.Role.valueOf(role));
                        redirectAttributes.addFlashAttribute("successMessage", "Cấp vai trò Staff thành công!");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Lỗi khi cấp vai trò Staff: " + e.getMessage());
                }
                return "redirect:/admin/users";
        }

        @PostMapping("/{id}/revoke-staff")
        public String revokeStaff(@PathVariable String id, RedirectAttributes redirectAttributes) {
                try {
                        userService.revokeStaffRole(id);
                        redirectAttributes.addFlashAttribute("successMessage", "Hủy vai trò Staff thành công!");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Lỗi khi hủy vai trò Staff: " + e.getMessage());
                }
                return "redirect:/admin/users";
        }
}

