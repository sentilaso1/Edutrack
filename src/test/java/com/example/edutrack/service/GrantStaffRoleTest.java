package com.example.edutrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.edutrack.accounts.service.implementations.UserServiceImpl;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.StaffRepository;
import com.example.edutrack.accounts.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrantStaffRole Method - Complete Test Suite")
class GrantStaffRoleTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private StaffRepository staffRepository;

        @Mock
        private MentorRepository mentorRepository;

        @Mock
        private MenteeRepository menteeRepository;

        @InjectMocks
        private UserServiceImpl userService;

        private User mockUser;
        private Staff mockStaff;
        private UUID validUUID;
        private String validUserId;

        @BeforeEach
        void setUp() {
                validUUID = UUID.randomUUID();
                validUserId = validUUID.toString();

                mockUser = new User();
                mockUser.setId(validUUID);
                mockUser.setEmail("test@example.com");
                mockUser.setFullName("Test User");
                mockUser.setIsActive(true);
                mockUser.setIsLocked(false);

                mockStaff = new Staff();
                mockStaff.setId(validUUID);
                mockStaff.setRole(Staff.Role.Manager);
                mockStaff.setCreatedDate(new Date());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId is null")
        void testGrantStaffRole_NullUserId() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.grantStaffRole(null, Staff.Role.Admin));

                assertEquals("User ID cannot be null or empty", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId is empty string")
        void testGrantStaffRole_EmptyUserId() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.grantStaffRole("", Staff.Role.Admin));

                assertEquals("User ID cannot be null or empty", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId is whitespace only")
        void testGrantStaffRole_WhitespaceUserId() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.grantStaffRole("   ", Staff.Role.Admin));

                assertEquals("User ID cannot be null or empty", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when role is null")
        void testGrantStaffRole_NullRole() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.grantStaffRole(validUserId, null));

                assertEquals("Role cannot be null", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId has invalid UUID format")
        void testGrantStaffRole_InvalidUUIDFormat() {
                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.grantStaffRole("invalid-uuid-format", Staff.Role.Admin));

                assertEquals("Invalid UUID format for user ID: invalid-uuid-format", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should handle userId with leading and trailing whitespace correctly")
        void testGrantStaffRole_UserIdWithWhitespace() {
                String userIdWithWhitespace = "  " + validUserId + "  ";
                setupSuccessfulCreationMocks();
                assertDoesNotThrow(() -> userService.grantStaffRole(userIdWithWhitespace, Staff.Role.Admin));

                verify(userRepository).findById(validUUID);
                verify(staffRepository).insertStaff(validUUID, "Admin");
        }

        @Test
        @DisplayName("Should throw RuntimeException when user is not found")
        void testGrantStaffRole_UserNotFound() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.empty());
                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("User not found with ID: " + validUserId, exception.getMessage());
                verify(userRepository).findById(validUUID);
                verifyNoInteractions(staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw RuntimeException when getUserById throws unexpected exception")
        void testGrantStaffRole_GetUserByIdException() {
                when(userRepository.findById(validUUID))
                                .thenThrow(new RuntimeException("Database connection error"));

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("User not found with ID: " + validUserId, exception.getMessage());
                verify(userRepository).findById(validUUID);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is inactive")
        void testGrantStaffRole_InactiveUser() {
                mockUser.setIsActive(false);
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Cannot grant staff role to inactive user", exception.getMessage());
                verify(userRepository).findById(validUUID);
                verifyNoMoreInteractions(staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is locked")
        void testGrantStaffRole_LockedUser() {
                mockUser.setIsLocked(true);
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Cannot grant staff role to locked user", exception.getMessage());
                verify(userRepository).findById(validUUID);
                verifyNoMoreInteractions(staffRepository, mentorRepository, menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user already has the same role")
        void testGrantStaffRole_ExistingStaffSameRole() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(true);
                when(staffRepository.findById(validUUID)).thenReturn(Optional.of(mockStaff));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Manager));

                assertEquals("User already has the role: Manager", exception.getMessage());
                verify(userRepository).findById(validUUID);
                verify(staffRepository).existsById(validUUID);
                verify(staffRepository).findById(validUUID);
                verifyNoMoreInteractions(staffRepository);
        }

        @Test
        @DisplayName("Should update existing staff role when user has different role")
        void testGrantStaffRole_ExistingStaffDifferentRole() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(true);
                when(staffRepository.findById(validUUID)).thenReturn(Optional.of(mockStaff));

                assertDoesNotThrow(() -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                verify(userRepository).findById(validUUID);
                verify(staffRepository).existsById(validUUID);
                verify(staffRepository).findById(validUUID);
                verify(staffRepository).save(any(Staff.class));

                assertEquals(Staff.Role.Admin, mockStaff.getRole());
                assertNotNull(mockStaff.getCreatedDate());
        }

        @Test
        @DisplayName("Should throw IllegalStateException when staff record not found for existing ID")
        void testGrantStaffRole_StaffNotFoundForExistingId() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(true);
                when(staffRepository.findById(validUUID)).thenReturn(Optional.empty());

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Staff record not found for existing ID: " + validUserId, exception.getMessage());
                verify(userRepository).findById(validUUID);
                verify(staffRepository).existsById(validUUID);
                verify(staffRepository).findById(validUUID);
        }

        @Test
        @DisplayName("Should throw RuntimeException when saving existing staff fails")
        void testGrantStaffRole_SaveExistingStaffFailure() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(true);
                when(staffRepository.findById(validUUID)).thenReturn(Optional.of(mockStaff));
                when(staffRepository.save(any(Staff.class)))
                                .thenThrow(new RuntimeException("Database save failed"));

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertTrue(exception.getMessage().contains("Database save failed"));
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is already a mentor")
        void testGrantStaffRole_UserIsMentor() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(false);
                when(mentorRepository.existsById(validUUID)).thenReturn(true);

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Cannot grant staff role to user who is already a Mentor", exception.getMessage());
                verify(userRepository).findById(validUUID);
                verify(staffRepository).existsById(validUUID);
                verify(mentorRepository).existsById(validUUID);
                verifyNoInteractions(menteeRepository);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is already a mentee")
        void testGrantStaffRole_UserIsMentee() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(false);
                when(mentorRepository.existsById(validUUID)).thenReturn(false);
                when(menteeRepository.existsById(validUUID)).thenReturn(true);

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Cannot grant staff role to user who is already a Mentee", exception.getMessage());
                verify(userRepository).findById(validUUID);
                verify(staffRepository).existsById(validUUID);
                verify(mentorRepository).existsById(validUUID);
                verify(menteeRepository).existsById(validUUID);
        }

        @Test
        @DisplayName("Should successfully create new staff when all conditions are met")
        void testGrantStaffRole_SuccessfulCreation() {
                setupSuccessfulCreationMocks();
                assertDoesNotThrow(() -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                verify(userRepository).findById(validUUID);
                verify(staffRepository).existsById(validUUID);
                verify(mentorRepository).existsById(validUUID);
                verify(menteeRepository).existsById(validUUID);
                verify(staffRepository).insertStaff(validUUID, "Admin");
        }

        @Test
        @DisplayName("Should handle all Staff.Role enum values correctly")
        void testGrantStaffRole_AllRoleEnumValues() {
                for (Staff.Role role : Staff.Role.values()) {
                        reset(userRepository, staffRepository, mentorRepository, menteeRepository);
                        setupSuccessfulCreationMocks();

                        // Act & Assert
                        assertDoesNotThrow(() -> userService.grantStaffRole(validUserId, role));
                        verify(staffRepository).insertStaff(validUUID, role.name());
                }
        }

        @Test
        @DisplayName("Should throw RuntimeException when database insertion fails")
        void testGrantStaffRole_DatabaseInsertionFailure() {
                setupSuccessfulCreationMocks();
                doThrow(new RuntimeException("Database connection failed"))
                                .when(staffRepository).insertStaff(any(UUID.class), anyString());

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Failed to grant staff role: Database connection failed", exception.getMessage());
                verify(staffRepository).insertStaff(validUUID, "Admin");
        }

        @Test
        @DisplayName("Should handle exception when checking mentor existence")
        void testGrantStaffRole_MentorRepositoryException() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(false);
                when(mentorRepository.existsById(validUUID))
                                .thenThrow(new RuntimeException("Mentor repository error"));

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Failed to grant staff role: Mentor repository error", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle exception when checking mentee existence")
        void testGrantStaffRole_MenteeRepositoryException() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(false);
                when(mentorRepository.existsById(validUUID)).thenReturn(false);
                when(menteeRepository.existsById(validUUID))
                                .thenThrow(new RuntimeException("Mentee repository error"));

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Failed to grant staff role: Mentee repository error", exception.getMessage());
        }

        @Test
        @DisplayName("Should preserve original exception cause in RuntimeException")
        void testGrantStaffRole_ExceptionCausePreserved() {
                RuntimeException originalException = new RuntimeException("Original database error");
                setupSuccessfulCreationMocks();
                doThrow(originalException)
                                .when(staffRepository).insertStaff(any(UUID.class), anyString());

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Failed to grant staff role: Original database error", exception.getMessage());
                assertEquals(originalException, exception.getCause());
        }

        @Test
        @DisplayName("Should rethrow IllegalStateException without wrapping")
        void testGrantStaffRole_IllegalStateExceptionNotWrapped() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(false);
                when(mentorRepository.existsById(validUUID))
                                .thenThrow(new IllegalStateException("State validation failed"));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("State validation failed", exception.getMessage());
        }

        @Test
        @DisplayName("Should handle valid UUID with different format variations")
        void testGrantStaffRole_ValidUUIDDifferentFormats() {
                String uppercaseUUID = validUserId.toUpperCase();
                setupSuccessfulCreationMocks();

                assertDoesNotThrow(() -> userService.grantStaffRole(uppercaseUUID, Staff.Role.Admin));
                verify(staffRepository).insertStaff(validUUID, "Admin");
        }

        @Test
        @DisplayName("Should handle concurrent modification scenarios gracefully")
        void testGrantStaffRole_ConcurrentModification() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID))
                                .thenReturn(false)
                                .thenReturn(true);
                when(mentorRepository.existsById(validUUID)).thenReturn(false);
                when(menteeRepository.existsById(validUUID)).thenReturn(false);
                doThrow(new RuntimeException("Duplicate key constraint"))
                                .when(staffRepository).insertStaff(any(UUID.class), anyString());

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.grantStaffRole(validUserId, Staff.Role.Admin));

                assertEquals("Failed to grant staff role: Duplicate key constraint", exception.getMessage());
        }

        private void setupSuccessfulCreationMocks() {
                when(userRepository.findById(validUUID)).thenReturn(Optional.of(mockUser));
                when(staffRepository.existsById(validUUID)).thenReturn(false);
                when(mentorRepository.existsById(validUUID)).thenReturn(false);
                when(menteeRepository.existsById(validUUID)).thenReturn(false);
        }
}