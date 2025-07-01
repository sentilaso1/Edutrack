package com.example.edutrack.service;

import com.example.edutrack.accounts.model.Staff;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.StaffRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.accounts.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceRevokeStaffRoleTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private StaffRepository staffRepository;

        @InjectMocks
        private UserServiceImpl userService;

        private UUID validUserId;
        private User activeUser;
        private User inactiveUser;
        private User lockedUser;
        private Staff regularStaff;
        private Staff adminStaff;

        @BeforeEach
        void setUp() {
                validUserId = UUID.randomUUID();

                // Setup active user
                activeUser = new User();
                activeUser.setId(validUserId);
                activeUser.setIsActive(true);
                activeUser.setIsLocked(false);
                activeUser.setEmail("active@test.com");

                // Setup inactive user
                inactiveUser = new User();
                inactiveUser.setId(validUserId);
                inactiveUser.setIsActive(false);
                inactiveUser.setIsLocked(false);
                inactiveUser.setEmail("inactive@test.com");

                // Setup locked user
                lockedUser = new User();
                lockedUser.setId(validUserId);
                lockedUser.setIsActive(true);
                lockedUser.setIsLocked(true);
                lockedUser.setEmail("locked@test.com");

                regularStaff = new Staff();
                regularStaff.setId(validUserId);
                regularStaff.setRole(Staff.Role.Manager);

                adminStaff = new Staff();
                adminStaff.setId(validUserId);
                adminStaff.setRole(Staff.Role.Admin);
        }

        @Test
        void testRevokeStaffRole_ValidInput_Success() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.of(activeUser));
                when(staffRepository.findById(validUserId)).thenReturn(Optional.of(regularStaff));

                assertDoesNotThrow(() -> userService.revokeStaffRole(validId));

                verify(userRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).deleteById(validUserId);
        }

        @Test
        void testRevokeStaffRole_NullId_ThrowsIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.revokeStaffRole(null));

                assertEquals("User ID cannot be empty!", exception.getMessage());

                verifyNoInteractions(userRepository, staffRepository);
        }

        @Test
        void testRevokeStaffRole_EmptyString_ThrowsIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.revokeStaffRole(""));

                assertEquals("User ID cannot be empty!", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository);
        }

        @Test
        void testRevokeStaffRole_WhitespaceOnly_ThrowsIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.revokeStaffRole("   "));

                assertEquals("User ID cannot be empty!", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository);
        }

        @Test
        void testRevokeStaffRole_InvalidUUIDFormat_ThrowsIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.revokeStaffRole("invalid-uuid-format"));

                assertEquals("Invalid user ID format!", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository);
        }

        @Test
        void testRevokeStaffRole_UserNotFound_ThrowsRuntimeException() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.empty());

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> userService.revokeStaffRole(validId));

                assertEquals("User not found!", exception.getMessage());

                verify(userRepository, times(1)).findById(validUserId);
                verifyNoInteractions(staffRepository);
        }

        @Test
        void testRevokeStaffRole_InactiveUser_ThrowsIllegalStateException() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.of(inactiveUser));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.revokeStaffRole(validId));

                assertEquals("Cannot revoke staff role from inactive user account!", exception.getMessage());

                verify(userRepository, times(1)).findById(validUserId);
                verifyNoInteractions(staffRepository);
        }

        @Test
        void testRevokeStaffRole_NoStaffRole_ThrowsIllegalStateException() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.of(activeUser));
                when(staffRepository.findById(validUserId)).thenReturn(Optional.empty());

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.revokeStaffRole(validId));

                assertEquals("User does not have staff role to revoke!", exception.getMessage());

                verify(userRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).findById(validUserId);
                verify(staffRepository, never()).deleteById(any());
        }

        @Test
        void testRevokeStaffRole_AdminRole_ThrowsIllegalStateException() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.of(activeUser));
                when(staffRepository.findById(validUserId)).thenReturn(Optional.of(adminStaff));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.revokeStaffRole(validId));

                assertEquals("Cannot revoke admin role! Admin privileges cannot be removed.", exception.getMessage());

                verify(userRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).findById(validUserId);
                verify(staffRepository, never()).deleteById(any());
        }

        @Test
        void testRevokeStaffRole_LockedUser_ThrowsIllegalStateException() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.of(lockedUser));
                when(staffRepository.findById(validUserId)).thenReturn(Optional.of(regularStaff));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.revokeStaffRole(validId));

                assertEquals("Cannot revoke staff role from locked user account!", exception.getMessage());

                verify(userRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).findById(validUserId);
                verify(staffRepository, never()).deleteById(any());
        }

        @Test
        void testRevokeStaffRole_ValidUUIDEdgeCases_Success() {
                UUID minUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
                User minUser = new User();
                minUser.setId(minUUID);
                minUser.setIsActive(true);
                minUser.setIsLocked(false);

                Staff minStaff = new Staff();
                minStaff.setId(minUUID);
                minStaff.setRole(Staff.Role.Manager);

                when(userRepository.findById(minUUID)).thenReturn(Optional.of(minUser));
                when(staffRepository.findById(minUUID)).thenReturn(Optional.of(minStaff));

                assertDoesNotThrow(() -> userService.revokeStaffRole(minUUID.toString()));

                verify(staffRepository, times(1)).deleteById(minUUID);
        }

        @Test
        void testRevokeStaffRole_CompleteHappyPath_AllValidationsPass() {
                String validId = validUserId.toString();

                activeUser.setIsActive(true);
                activeUser.setIsLocked(false);

                regularStaff.setRole(Staff.Role.Manager);

                when(userRepository.findById(validUserId)).thenReturn(Optional.of(activeUser));
                when(staffRepository.findById(validUserId)).thenReturn(Optional.of(regularStaff));

                assertDoesNotThrow(() -> userService.revokeStaffRole(validId));

                verify(userRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).findById(validUserId);
                verify(staffRepository, times(1)).deleteById(validUserId);

                var inOrder = inOrder(userRepository, staffRepository);
                inOrder.verify(userRepository).findById(validUserId);
                inOrder.verify(staffRepository).findById(validUserId);
                inOrder.verify(staffRepository).deleteById(validUserId);
        }

        @Test
        void testRevokeStaffRole_SpecialCharactersInUUID_ThrowsIllegalArgumentException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.revokeStaffRole("12345678-1234-1234-1234-12345678901@"));

                assertEquals("Invalid user ID format!", exception.getMessage());
                verifyNoInteractions(userRepository, staffRepository);
        }

        @Test
        void testRevokeStaffRole_DifferentStaffRoles_OnlyRegularStaffSuccess() {
                String validId = validUserId.toString();
                when(userRepository.findById(validUserId)).thenReturn(Optional.of(activeUser));

                Staff[] staffRoles = {
                                createStaff(Staff.Role.Manager),
                                createStaff(Staff.Role.Admin)
                };

                when(staffRepository.findById(validUserId)).thenReturn(Optional.of(staffRoles[0]));
                assertDoesNotThrow(() -> userService.revokeStaffRole(validId));

                reset(staffRepository);
                when(staffRepository.findById(validUserId)).thenReturn(Optional.of(staffRoles[1]));
                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> userService.revokeStaffRole(validId));
                assertEquals("Cannot revoke admin role! Admin privileges cannot be removed.", exception.getMessage());
        }

        private Staff createStaff(Staff.Role role) {
                Staff staff = new Staff();
                staff.setId(validUserId);
                staff.setRole(role);
                return staff;
        }
}
