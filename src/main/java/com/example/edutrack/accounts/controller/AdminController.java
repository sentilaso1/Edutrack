package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.RequestLogRepository;
import com.example.edutrack.accounts.model.RequestLog;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import com.example.edutrack.accounts.service.interfaces.UserService;
import com.example.edutrack.accounts.dto.UserFilter;
import com.example.edutrack.accounts.dto.UserWithRoleDTO;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/admin")
public class AdminController {

        private final UserService userService;
        private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
        private final SystemConfigService systemConfigService;
        @Autowired
        private RequestLogRepository requestLogRepository;


        @Autowired
        public AdminController(UserService userService, SystemConfigService systemConfigService) {
                this.userService = userService;
                this.systemConfigService = systemConfigService;
        }

        @GetMapping("/users")
        public String showUserManagement(Model model,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String fullName,
                                        @RequestParam(required = false) Boolean isLocked,
                                        @RequestParam(required = false) Boolean isActive,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<User> userPage = userService.searchUsers(email, fullName, isLocked, isActive, pageable);
                List<UserWithRoleDTO> userDtos = new ArrayList<>();

                for (User user : userPage.getContent()) {
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
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", userPage.getTotalPages());
                model.addAttribute("totalItems", userPage.getTotalElements());
                model.addAttribute("pageSize", size);
                return "accounts/html/user-management";
        }

        @PostMapping("/users/{id}/lock")
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

        @PostMapping("/users/{id}/activate")
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

        @PostMapping("/users/{id}/grant-staff")
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

        @PostMapping("/users/{id}/revoke-staff")      
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

        @GetMapping("/dashboard")
        public String showDashboard(Model model) {
                logger.info("Accessing admin dashboard");
                model.addAttribute("systemStatus", systemConfigService.getSystemStatus());
                return "accounts/html/index.html";
        }

        @GetMapping("/system-settings")
        public String showSystemSettings(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
                Pageable pageable = PageRequest.of(page, size);
                Page<RequestLog> logPage = requestLogRepository.findAll(pageable);
                logger.info("Accessing system settings");
                model.addAttribute("configs", systemConfigService.getSystemConfigs());
                model.addAttribute("systemStatus", systemConfigService.getSystemStatus());
                model.addAttribute("logPage", logPage);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", logPage.getTotalPages());
                return "accounts/html/system-settings";
        }

        @PostMapping("/system-settings/update")
        public String updateSystemSettings(@RequestParam String key, @RequestParam String value) {
                logger.info("Updating system config: {} = {}", key, value);
                systemConfigService.updateConfig(key, value);
                return "redirect:/admin/system-settings?success=Configuration updated successfully";
        }
}

