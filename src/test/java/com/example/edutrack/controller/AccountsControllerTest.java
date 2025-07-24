package com.example.edutrack.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.edutrack.accounts.controller.AccountsController;
import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.model.User;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.accounts.service.interfaces.ProfileService;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountsControllerTest {
        @Mock
        private ProfileService userService;

        @Mock
        private MentorService mentorService;

        @Mock
        private MenteeService menteeService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private MentorRepository mentorRepository;

        @Mock
        private MenteeRepository menteeRepository;

        @Mock
        private RedirectAttributes redirectAttributes;

        @Mock
        private HttpSession session;

        @InjectMocks
        private AccountsController controller;

        private String validId;
        private User mockUser;
        private User loggedInUser;
        private Mentor mockMentor;
        private Mentee mockMentee;

        // Constants for test data
        private static final String VALID_FULL_NAME = "John Doe";
        private static final String VALID_PHONE = "0123456789";
        private static final String VALID_BIRTH_DATE = "1990-01-01";
        private static final String VALID_BIO = "bio";
        private static final String VALID_GENDER = "Male";
        private static final String VALID_EXPERTISE = "Java";
        private static final String VALID_INTERESTS = "Programming";

        @BeforeEach
        void setUp() {
                validId = UUID.randomUUID().toString();
                mockUser = new User();
                mockUser.setId(UUID.fromString(validId));

                loggedInUser = new User();
                loggedInUser.setId(UUID.fromString(validId));

                mockMentor = new Mentor();
                mockMentee = new Mentee();

                // Setup session to return loggedInUser
                when(session.getAttribute("loggedInUser")).thenReturn(loggedInUser);
        }

        // Test Case 5.1: Full name validation - null fullName
        @Test
        void testEditProfile_FullNameNull_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, null, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Full name is required");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.2: Full name validation - empty fullName
        @Test
        void testEditProfile_FullNameEmpty_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, "", VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Full name is required");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.3: Full name validation - whitespace only fullName
        @Test
        void testEditProfile_FullNameWhitespace_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, "   ", VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Full name is required");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.4: Phone validation - invalid phone format (too short)
        @Test
        void testEditProfile_InvalidPhoneShort_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, "012345", VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Phone number is not valid (must start with 0 and be 10 or 11 digits)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.5: Phone validation - invalid phone format (too long)
        @Test
        void testEditProfile_InvalidPhoneLong_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, "012345678901", VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Phone number is not valid (must start with 0 and be 10 or 11 digits)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.6: Phone validation - invalid phone format (not starting with 0)
        @Test
        void testEditProfile_InvalidPhoneNotStartWithZero_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, "1123456789", VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Phone number is not valid (must start with 0 and be 10 or 11 digits)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.7: Phone validation - null phone
        @Test
        void testEditProfile_NullPhone_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, null, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Phone number is not valid (must start with 0 and be 10 or 11 digits)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.8: Birth date validation - future date
        @Test
        void testEditProfile_FutureBirthDate_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                "2030-01-01", VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Birth date is not valid (must be a past date in yyyy-MM-dd format)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.9: Birth date validation - invalid format
        @Test
        void testEditProfile_InvalidBirthDateFormat_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                "01/01/1990", VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Birth date is not valid (must be a past date in yyyy-MM-dd format)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.10: Birth date validation - null birth date
        @Test
        void testEditProfile_NullBirthDate_ReturnsRedirectWithError() throws IOException {
                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                null, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Birth date is not valid (must be a past date in yyyy-MM-dd format)");
                verifyNoInteractions(userService, mentorService, menteeService);
        }

        // Test Case 5.11: User not found
        @Test
        void testEditProfile_UserNotFound_ThrowsException() throws IOException {
                when(userService.getUserById(validId)).thenReturn(null);

                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        controller.editProfile(
                                        session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                        VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS,
                                        redirectAttributes);
                });

                assertEquals("User not found with id: " + validId, exception.getMessage());
        }

        // Test Case 5.12: Mentor with empty expertise
        @Test
        void testEditProfile_MentorWithEmptyExpertise_ReturnsRedirectWithError() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, "", VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Expertise is required");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.13: Mentor with null expertise
        @Test
        void testEditProfile_MentorWithNullExpertise_ReturnsRedirectWithError() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Expertise is required");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.14: Mentor with whitespace expertise
        @Test
        void testEditProfile_MentorWithWhitespaceExpertise_ReturnsRedirectWithError() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, "   ", VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Expertise is required");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.15: Successful mentor profile update
        @Test
        void testEditProfile_SuccessfulMentorUpdate_ReturnsRedirect() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, VALID_EXPERTISE, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals(VALID_BIO)));
                verify(mentorRepository).save(argThat(mentor -> mentor.getExpertise().equals(VALID_EXPERTISE)));
        }

        // Test Case 5.16: Successful mentee profile update
        @Test
        void testEditProfile_SuccessfulMenteeUpdate_ReturnsRedirect() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, "Jane Doe", "0987654321", "student bio",
                                "1995-05-15", "Female", null, "Machine Learning", redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals("Jane Doe") &&
                                user.getPhone().equals("0987654321") &&
                                user.getBio().equals("student bio")));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals("Machine Learning")));
        }

        // Test Case 5.17: Valid phone boundary - 10 digits
        @Test
        void testEditProfile_ValidPhone10Digits_Success() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals(VALID_BIO)));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.18: Valid phone boundary - 11 digits
        @Test
        void testEditProfile_ValidPhone11Digits_Success() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, "01234567890", VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals("01234567890") &&
                                user.getBio().equals(VALID_BIO)));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.19: Null bio handling
        @Test
        void testEditProfile_NullBio_Success() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, null,
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio() == null));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.20: Empty bio handling
        @Test
        void testEditProfile_EmptyBio_Success() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, "",
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals("")));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.21: Bio with whitespace trimming
        @Test
        void testEditProfile_BioWithWhitespace_TrimsAndSuccess() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, "  bio with spaces  ",
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals("bio with spaces")));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.22: Valid birth date boundary - past date
        @Test
        void testEditProfile_BirthDateYesterday_Success() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                "2020-01-01", VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals(VALID_BIO)));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.23: FullName with leading/trailing whitespace
        @Test
        void testEditProfile_FullNameWithWhitespace_TrimsAndSuccess() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, "  " + VALID_FULL_NAME + "  ", VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals(VALID_BIO)));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests().equals(VALID_INTERESTS)));
        }

        // Test Case 5.24: Mentor with expertise too short
        @Test
        void testEditProfile_MentorWithExpertiseTooShort_ReturnsRedirectWithError() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, "Ja", VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Expertise must be between 3 and 100 characters");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.25: Mentor with expertise too long
        @Test
        void testEditProfile_MentorWithExpertiseTooLong_ReturnsRedirectWithError() throws IOException {
                String longExpertise = "a".repeat(101); // 101 characters
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, longExpertise, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Expertise must be between 3 and 100 characters");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.26: Mentee with interests too short
        @Test
        void testEditProfile_MenteeWithInterestsTooShort_ReturnsRedirectWithError() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, "AI", redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Interests must be between 3 and 100 characters");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.27: Mentee with interests too long
        @Test
        void testEditProfile_MenteeWithInterestsTooLong_ReturnsRedirectWithError() throws IOException {
                String longInterests = "a".repeat(101); // 101 characters
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, longInterests, redirectAttributes);

                assertEquals("redirect:/profile#edit", result);
                verify(redirectAttributes).addFlashAttribute("error", "Interests must be between 3 and 100 characters");
                verifyNoInteractions(userRepository, mentorRepository, menteeRepository);
        }

        // Test Case 5.28: Mentee with null interests
        @Test
        void testEditProfile_MenteeWithNullInterests_Success() throws IOException {
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(null);
                when(menteeService.getMenteeById(validId)).thenReturn(mockMentee);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, null, null, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals(VALID_BIO)));
                verify(menteeRepository).save(argThat(mentee -> mentee.getInterests() == null));
        }

        // Test Case 5.29: Mentor with expertise exactly 100 characters
        @Test
        void testEditProfile_MentorWithExpertiseExactly100Chars_Success() throws IOException {
                String exactExpertise = "a".repeat(100); // Exactly 100 characters
                when(userService.getUserById(validId)).thenReturn(mockUser);
                when(mentorService.getMentorById(validId)).thenReturn(mockMentor);

                String result = controller.editProfile(
                                session, VALID_FULL_NAME, VALID_PHONE, VALID_BIO,
                                VALID_BIRTH_DATE, VALID_GENDER, exactExpertise, VALID_INTERESTS, redirectAttributes);

                assertEquals("redirect:/profile", result);
                verify(userRepository).save(argThat(user -> user.getFullName().equals(VALID_FULL_NAME) &&
                                user.getPhone().equals(VALID_PHONE) &&
                                user.getBio().equals(VALID_BIO)));
                verify(mentorRepository).save(argThat(mentor -> mentor.getExpertise().equals(exactExpertise)));
        }
}