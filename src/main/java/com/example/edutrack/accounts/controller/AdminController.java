package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.RequestLogRepository;
import com.example.edutrack.accounts.model.RequestLog;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import com.example.edutrack.accounts.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import com.example.edutrack.accounts.dto.ScheduledJobDTO;
import com.example.edutrack.accounts.dto.UserFilter;
import com.example.edutrack.accounts.dto.UserWithRoleDTO;
import com.example.edutrack.accounts.service.interfaces.ScheduledJobService;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
        private final UserService userService;
        private final SystemConfigService systemConfigService;
        private final ScheduledJobService scheduledJobService;
        @Autowired
        private RequestLogRepository requestLogRepository;

        @Autowired
        public AdminController(UserService userService, SystemConfigService systemConfigService,
                        ScheduledJobService scheduledJobService) {
                this.userService = userService;
                this.systemConfigService = systemConfigService;
                this.scheduledJobService = scheduledJobService;
        }

        @GetMapping("/users")
        public String showUserManagement(Model model,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) String fullName,
                        @RequestParam(required = false) Boolean isLocked,
                        @RequestParam(required = false) Boolean isActive,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
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

        // Function 2
        @PostMapping("/users/{id}/lock")
        public String toggleLock(@PathVariable String id, RedirectAttributes redirectAttributes) {
                try {
                        if (id == null || id.trim().isEmpty()) {
                                redirectAttributes.addFlashAttribute("errorMessage", "User ID cannot be empty!");
                                return "redirect:/admin/users";
                        }

                        UUID userId;
                        try {
                                userId = UUID.fromString(id);
                        } catch (IllegalArgumentException e) {
                                redirectAttributes.addFlashAttribute("errorMessage", "Invalid user ID format!");
                                return "redirect:/admin/users";
                        }

                        User user = userService.getUserById(id);
                        if (user == null) {
                                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
                                return "redirect:/admin/users";
                        }

                        if (!user.getIsActive()) {
                                redirectAttributes.addFlashAttribute("errorMessage",
                                                "Cannot lock/unlock inactive user!");
                                return "redirect:/admin/users";
                        }

                        Staff staff = userService.getStaffByUserId(id);
                        if (staff != null && staff.getRole() == Staff.Role.Admin) {
                                redirectAttributes.addFlashAttribute("errorMessage",
                                                "Cannot lock/unlock admin account!");
                                return "redirect:/admin/users";
                        }

                        boolean currentLockState = user.getIsLocked();
                        user.setIsLocked(!currentLockState);
                        try {
                                userService.saveUser(user);
                        } catch (IllegalArgumentException e) {
                                redirectAttributes.addFlashAttribute("errorMessage",
                                                "Validation error when saving: " + e.getMessage());
                                return "redirect:/admin/users";
                        }
                        if (user.getIsLocked()) {
                                redirectAttributes.addFlashAttribute("successMessage", "User locked successfully!");
                        } else {
                                redirectAttributes.addFlashAttribute("successMessage", "User unlocked successfully!");
                        }

                } catch (RuntimeException e) {
                        if (e.getMessage().contains("User not found")) {
                                redirectAttributes.addFlashAttribute("errorMessage", "User not found in system!");
                        } else {
                                redirectAttributes.addFlashAttribute("errorMessage",
                                                "System error when changing lock status: " + e.getMessage());
                        }
                        return "redirect:/admin/users";
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Unknown error when changing lock status: " + e.getMessage());
                        return "redirect:/admin/users";
                }

                return "redirect:/admin/users";
        }

        @PostMapping("/users/{id}/activate")
        public String toggleActivate(@PathVariable String id, RedirectAttributes redirectAttributes) {
                try {
                        User user = userService.getUserById(id);
                        if (user == null) {
                                redirectAttributes.addFlashAttribute("errorMessage",
                                                "Can not find user with ID: " + id);
                                return "redirect:/admin/users";
                        }
                        user.setIsActive(!user.getIsActive()); // Toggle active state
                        userService.saveUser(user);
                        redirectAttributes.addFlashAttribute("successMessage",
                                        user.getIsActive() ? "Activate user successfully!"
                                                        : "Deactivate user successfully!");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Error when change status: " + e.getMessage());
                }
                return "redirect:/admin/users";
        }

        @PostMapping("/users/{id}/grant-staff")
        public String grantStaff(@PathVariable String id, @RequestParam String role,
                        RedirectAttributes redirectAttributes) {
                try {
                        Staff.Role staffRole = Staff.Role.valueOf(role);
                        userService.grantStaffRole(id, staffRole);
                        redirectAttributes.addFlashAttribute("successMessage", "Staff role granted successfully!");
                } catch (IllegalArgumentException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Invalid role");
                } catch (IllegalStateException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Error when grant staff role: " + e.getMessage());
                }
                return "redirect:/admin/users";
        }

        @PostMapping("/users/{id}/revoke-staff")
        public String revokeStaff(@PathVariable String id, RedirectAttributes redirectAttributes) {
                try {
                        userService.revokeStaffRole(id);
                        redirectAttributes.addFlashAttribute("successMessage", "Staff role revoked successfully!");

                } catch (IllegalArgumentException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                        return "redirect:/admin/users";
                } catch (IllegalStateException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                        return "redirect:/admin/users";
                } catch (RuntimeException e) {
                        if (e.getMessage().contains("User not found")) {
                                redirectAttributes.addFlashAttribute("errorMessage", "User not found in system!");
                        } else {
                                redirectAttributes.addFlashAttribute("errorMessage",
                                                "System error when revoking staff role: " + e.getMessage());
                        }
                        return "redirect:/admin/users";
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "Unknown error when revoking staff role: " + e.getMessage());
                        return "redirect:/admin/users";
                }
                return "redirect:/admin/users";
        }

        @GetMapping("/users/export-log")
        public void exportUser(HttpServletResponse response) throws IOException {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
                userService.exportToCsv(response.getWriter());
        }

        @GetMapping("/dashboard")
        public String showDashboard(Model model) {
                model.addAttribute("systemStatus", systemConfigService.getSystemStatus());
                model.addAttribute("userStats", userService.getUserStatistics());
                model.addAttribute("jobStats", scheduledJobService.getJobSummary());
                return "accounts/html/index.html";
        }

        @GetMapping("/system-settings")
        public String showSystemSettings(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String ip,
                        @RequestParam(required = false) String method,
                        @RequestParam(required = false) String uri,
                        Model model) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
                ;
                Page<RequestLog> logPage = requestLogRepository.filterLogs(
                                (ip != null && !ip.isEmpty()) ? ip : null,
                                (method != null && !method.isEmpty()) ? method : null,
                                (uri != null && !uri.isEmpty()) ? uri : null,
                                pageable);
                int totalPages = logPage.getTotalPages();
                int currentPage = page;

                int startPage = Math.max(0, currentPage - 2);
                int endPage = Math.min(totalPages - 1, currentPage + 2);

                Map<String, String> configs = systemConfigService.getConfigs("smtp.host", "smtp.port", "app.email",
                                "app.name");
                model.addAttribute("configs", configs);
                model.addAttribute("systemStatus", systemConfigService.getSystemStatus());
                model.addAttribute("logPage", logPage);
                model.addAttribute("currentPage", currentPage);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("startPage", startPage);
                model.addAttribute("endPage", endPage);
                model.addAttribute("pageSize", size);

                model.addAttribute("filterIp", ip);
                model.addAttribute("filterMethod", method);
                model.addAttribute("filterUri", uri);
                return "accounts/html/system-settings";
        }

        @PostMapping("/system-settings/update")
        public String updateSystemSettings(@RequestParam String key, @RequestParam String value) {
                systemConfigService.updateValue(key, value);
                return "redirect:/admin/system-settings?success=Configuration updated successfully";
        }

        @PostMapping("/system-settings/import-log")
        public String importLog(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
                systemConfigService.importFromCsv(file);
                redirectAttributes.addAttribute("success", "Logs imported successfully.");
                return "redirect:/admin/system-settings";
        }

        @GetMapping("/system-settings/export-log")
        public void exportLog(HttpServletResponse response) throws IOException {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"logs.csv\"");
                systemConfigService.exportToCsv(response.getWriter());
        }

        @PostMapping("/system-settings/clear-logs")
        public String clearLogs(RedirectAttributes redirectAttributes) {
                systemConfigService.clearAllLogs();
                redirectAttributes.addAttribute("success", "All logs cleared.");
                return "redirect:/admin/system-settings";
        }

        @GetMapping("/jobs")
        public String viewJobs(@RequestParam(required = false) String search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
                Pageable pageable = PageRequest.of(page, size);
                Page<ScheduledJobDTO> jobPage = scheduledJobService.getJobs(search, pageable);

                model.addAttribute("jobs", jobPage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", jobPage.getTotalPages());
                model.addAttribute("search", search);
                return "accounts/html/scheduled-jobs";
        }

        @PostMapping("/jobs/{id}/toggle")
        public String toggleJob(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes redirectAttributes) {
                try {
                        scheduledJobService.toggleJob(id, active);
                        redirectAttributes.addFlashAttribute("successMessage", "Job status updated successfully.");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                }
                return "redirect:/admin/jobs";
        }

        @PostMapping("/jobs/{id}/run")
        public String runJobNow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                try {
                        scheduledJobService.runJobNow(id);
                        redirectAttributes.addFlashAttribute("successMessage", "Job is running now.");
                } catch (IllegalStateException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Job is not active");
                } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Error running job");
                }
                return "redirect:/admin/jobs";
        }

        @PostMapping("/jobs/{id}/update")
        public String updateJob(@PathVariable Long id, @ModelAttribute ScheduledJobDTO dto, RedirectAttributes redirectAttributes) {
                scheduledJobService.updateJob(id, dto);
                redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully");
                return "redirect:/admin/jobs";
        }
}
