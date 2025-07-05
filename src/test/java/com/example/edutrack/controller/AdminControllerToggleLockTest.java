package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.service.interfaces.UserService;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import com.example.edutrack.accounts.service.interfaces.ScheduledJobService;
import com.example.edutrack.accounts.repository.RequestLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
import com.example.edutrack.accounts.controller.AdminController;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerToggleLockTest {

        @Mock
        private UserService userService;

        @Mock
        private SystemConfigService systemConfigService;

        @Mock
        private ScheduledJobService scheduledJobService;

        @Mock
        private RequestLogRepository requestLogRepository;

        @Mock
        private RedirectAttributes redirectAttributes;

        @InjectMocks
        private AdminController adminController;

        private User testUser;
        private Staff testStaff;
        private String validUserId;

        @BeforeEach
        void setUp() {
                validUserId = UUID.randomUUID().toString();

                testUser = new User();
                testUser.setId(UUID.fromString(validUserId));
                testUser.setEmail("test@example.com");
                testUser.setFullName("Test User");
                testUser.setIsActive(true);
                testUser.setIsLocked(false);

                testStaff = new Staff();
                testStaff.setRole(Staff.Role.Manager);
        }

        // TC 2.1
        @Test
        void testToggleLock_ManagerUser_Success() {
                // Arrange
                testUser.setIsLocked(false);
                testStaff.setRole(Staff.Role.Manager);
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                assertTrue(testUser.getIsLocked());
                verify(userService).saveUser(testUser);
                verify(redirectAttributes).addFlashAttribute("successMessage", "User locked successfully!");
        }
        // TC 2.2
        @Test
        void testToggleLock_NullId_ShouldReturnErrorMessage() {
                // Arrange
                String nullId = null;

                // Act
                String result = adminController.toggleLock(nullId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "User ID cannot be empty!");
                verifyNoInteractions(userService);
        }
        // TC 2.3
        @Test
        void testToggleLock_EmptyId_ShouldReturnErrorMessage() {
                // Arrange
                String emptyId = "";

                // Act
                String result = adminController.toggleLock(emptyId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "User ID cannot be empty!");
                verifyNoInteractions(userService);
        }
        // TC 2.4
        @Test
        void testToggleLock_WhitespaceId_ShouldReturnErrorMessage() {
                // Arrange
                String whitespaceId = "   ";

                // Act
                String result = adminController.toggleLock(whitespaceId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "User ID cannot be empty!");
                verifyNoInteractions(userService);
        }
        // TC 2.5
        @Test
        void testToggleLock_InvalidUuidFormat_ShouldReturnErrorMessage() {
                // Arrange
                String invalidId = "invalid-uuid-format";

                // Act
                String result = adminController.toggleLock(invalidId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid user ID format!");
                verifyNoInteractions(userService);
        }
        // TC 2.6
        @Test
        void testToggleLock_UserNotFound_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId)).thenReturn(null);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "User not found!");
                verify(userService).getUserById(validUserId);
        }
        // TC 2.7
        @Test
        void testToggleLock_InactiveUser_ShouldReturnErrorMessage() {
                // Arrange
                testUser.setIsActive(false);
                when(userService.getUserById(validUserId)).thenReturn(testUser);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "Cannot lock/unlock inactive user!");
                verify(userService).getUserById(validUserId);
                verify(userService, never()).getStaffByUserId(any());
        }
        // TC 2.8
        @Test
        void testToggleLock_AdminUser_ShouldReturnErrorMessage() {
                // Arrange
                testStaff.setRole(Staff.Role.Admin);
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "Cannot lock/unlock admin account!");
                verify(userService).getUserById(validUserId);
                verify(userService).getStaffByUserId(validUserId);
                verify(userService, never()).saveUser(any());
        }
        // TC 2.9
        @Test
        void testToggleLock_LockUser_Success() {
                // Arrange
                testUser.setIsLocked(false); // Initially unlocked
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff); // Non-admin staff (Manager)
                                                                                       // (Manager)

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                assertTrue(testUser.getIsLocked()); // Should be locked now
                verify(userService).saveUser(testUser);
                verify(redirectAttributes).addFlashAttribute("successMessage", "User locked successfully!");
        }
        // TC 2.10
        @Test
        void testToggleLock_UnlockUser_Success() {
                // Arrange
                testUser.setIsLocked(true); // Initially locked
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff); // Non-admin staff

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                assertFalse(testUser.getIsLocked()); // Should be unlocked now
                verify(userService).saveUser(testUser);
                verify(redirectAttributes).addFlashAttribute("successMessage", "User unlocked successfully!");
        }
        // TC 2.11
        @Test
        void testToggleLock_NonStaffUser_Success() {
                // Arrange
                testUser.setIsLocked(false);
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(null); // Not a staff member

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                assertTrue(testUser.getIsLocked());
                verify(userService).saveUser(testUser);
                verify(redirectAttributes).addFlashAttribute("successMessage", "User locked successfully!");
        }
        // TC 2.12
        @Test
        void testToggleLock_ValidationErrorOnSave_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff);
                doThrow(new IllegalArgumentException("Validation failed")).when(userService).saveUser(testUser);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage",
                                "Validation error when saving: Validation failed");
        }
        // TC 2.13
        @Test
        void testToggleLock_RuntimeExceptionUserNotFound_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId))
                                .thenThrow(new RuntimeException("User not found in database"));

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage", "User not found in system!");
        }
        // TC 2.14
        @Test
        void testToggleLock_RuntimeExceptionOther_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId))
                                .thenThrow(new RuntimeException("Database connection failed"));

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage",
                                "System error when changing lock status: Database connection failed");
        }
        // TC 2.15
        @Test
        void testToggleLock_NullPointerException_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId))
                                .thenThrow(new NullPointerException("Null pointer error"));

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage",
                                "System error when changing lock status: Null pointer error");
        }
        // TC 2.16
        @Test
        void testToggleLock_RuntimeExceptionDuringSave_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff);
                doThrow(new RuntimeException("Save operation failed")).when(userService).saveUser(testUser);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage",
                                "System error when changing lock status: Save operation failed");
        }
        // TC 2.17
        @Test
        void testToggleLock_UncheckedExceptionDuringSave_ShouldReturnErrorMessage() {
                // Arrange
                when(userService.getUserById(validUserId)).thenReturn(testUser);
                when(userService.getStaffByUserId(validUserId)).thenReturn(testStaff);
                doThrow(new IllegalStateException("Invalid state error")).when(userService).saveUser(testUser);

                // Act
                String result = adminController.toggleLock(validUserId, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("errorMessage",
                                "System error when changing lock status: Invalid state error");
        }
        // TC 2.18
        @Test   
        void testToggleLock_BoundaryValidUuid_Success() {
                // Arrange - Test with minimum valid UUID
                String minValidUuid = "00000000-0000-0000-0000-000000000000";
                testUser.setId(UUID.fromString(minValidUuid));
                when(userService.getUserById(minValidUuid)).thenReturn(testUser);
                when(userService.getStaffByUserId(minValidUuid)).thenReturn(null);

                // Act
                String result = adminController.toggleLock(minValidUuid, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("successMessage", "User locked successfully!");
        }
        // TC 2.19
        @Test
        void testToggleLock_BoundaryMaxValidUuid_Success() {
                // Arrange - Test with maximum valid UUID
                String maxValidUuid = "ffffffff-ffff-ffff-ffff-ffffffffffff";
                testUser.setId(UUID.fromString(maxValidUuid));
                when(userService.getUserById(maxValidUuid)).thenReturn(testUser);
                when(userService.getStaffByUserId(maxValidUuid)).thenReturn(null);

                // Act
                String result = adminController.toggleLock(maxValidUuid, redirectAttributes);

                // Assert
                assertEquals("redirect:/admin/users", result);
                verify(redirectAttributes).addFlashAttribute("successMessage", "User locked successfully!");
        }
}