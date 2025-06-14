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
                model.addAttribute("systemStatus", systemConfigService.getSystemStatus());
                model.addAttribute("userStats", userService.getUserStatistics());
                model.addAttribute("loginStats", userService.getLoginStats());
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
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));;
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
        public String viewJobs( @RequestParam(required = false) String search,
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
        public String toggleJob(@PathVariable Long id, @RequestParam boolean active) {
                scheduledJobService.toggleJob(id, active);
                return "redirect:/admin/jobs";
        }

        @PostMapping("/jobs/{id}/run")
        public String runJobNow(@PathVariable Long id) {
                scheduledJobService.runJobNow(id);
                return "redirect:/admin/jobs";
        }

        @PostMapping("/jobs/{id}/update")
        public String updateJob(@PathVariable Long id, @ModelAttribute ScheduledJobDTO dto) {
                scheduledJobService.updateJob(id, dto);
                return "redirect:/admin/jobs";
        }
}

