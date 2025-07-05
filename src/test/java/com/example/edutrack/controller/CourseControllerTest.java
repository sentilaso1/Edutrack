package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.implementations.MenteeServiceImpl;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.controller.CourseController;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.curriculum.service.implementation.CourseServiceImpl;
import com.example.edutrack.curriculum.service.implementation.CourseTagServiceImpl;
import com.example.edutrack.curriculum.service.implementation.TagServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.MentorAvailableTimeDetailsRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.transactions.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CourseControllerTest {

    private CourseController controller;
    private MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository;

    private Mentor mentor;
    private Mentee mentee;
    private LocalDate today;

    @BeforeEach
    void setup() {
        CourseServiceImpl courseServiceImpl = Mockito.mock(CourseServiceImpl.class);
        CourseTagServiceImpl courseTagServiceImpl = Mockito.mock(CourseTagServiceImpl.class);
        CourseTagService courseTagService = Mockito.mock(CourseTagService.class);
        MentorServiceImpl mentorServiceImpl = Mockito.mock(MentorServiceImpl.class);
        TagServiceImpl tagServiceImpl = Mockito.mock(TagServiceImpl.class);
        CourseMentorService courseMentorService = Mockito.mock(CourseMentorService.class);
        CourseMentorServiceImpl courseMentorServiceImpl = Mockito.mock(CourseMentorServiceImpl.class);
        MentorAvailableTimeService mentorAvailableTimeService = Mockito.mock(MentorAvailableTimeService.class);
        EnrollmentService enrollmentService = Mockito.mock(EnrollmentService.class);
        WalletService walletService = Mockito.mock(WalletService.class);
        CourseRepository courseRepository = Mockito.mock(CourseRepository.class);
        CourseMentorRepository courseMentorRepository = Mockito.mock(CourseMentorRepository.class);
        MenteeServiceImpl menteeService = Mockito.mock(MenteeServiceImpl.class);
        mentorAvailableTimeDetailsRepository = Mockito.mock(MentorAvailableTimeDetailsRepository.class);

        controller = new CourseController(
                courseServiceImpl,
                courseTagServiceImpl,
                mentorServiceImpl,
                tagServiceImpl,
                courseTagService,
                courseMentorService,
                courseMentorServiceImpl,
                mentorAvailableTimeService,
                enrollmentService,
                walletService,
                courseRepository,
                courseMentorRepository,
                menteeService,
                mentorAvailableTimeDetailsRepository
        );

        mentor = new Mentor();
        mentee = new Mentee();
        today = LocalDate.of(2025, 6, 28);
    }

    // availableSlotMatrix
//    @Test
//    void testAvailableSlotMatrix_withDynamicValidation() {
//        LocalDate minDate = LocalDate.of(2025, 7, 1);
//        LocalDate maxDate = LocalDate.of(2025, 7, 31);
//
//        // TC 3.1 Valid Slot with no clash mentor and mentor schedule with boundary value
//        LocalDate slotDate1 = LocalDate.of(2025, 7, 1);
//        when(mentorAvailableTimeDetailsRepository.existsByMentorAndSlotAndDateAndMenteeIsNull(mentor, Slot.SLOT_3, slotDate1)).thenReturn(true);
//        when(mentorAvailableTimeDetailsRepository.existsBySlotAndDateAndMentee(Slot.SLOT_3, slotDate1, mentee)).thenReturn(false);
//
//        // TC 3.2 Invalid Slot with no clash mentor but clash mentee schedule
//        LocalDate slotDate2 = LocalDate.of(2025, 7, 15);
//        when(mentorAvailableTimeDetailsRepository.existsByMentorAndSlotAndDateAndMenteeIsNull(mentor, Slot.SLOT_2, slotDate2)).thenReturn(true);
//        when(mentorAvailableTimeDetailsRepository.existsBySlotAndDateAndMentee(Slot.SLOT_2, slotDate2, mentee)).thenReturn(true); // mentee booked => false
//
//        // TC 3.3 Valid Slot with no clash mentor and mentor schedule with boundary value
//        LocalDate slotDate3 = LocalDate.of(2025, 7, 31);
//        when(mentorAvailableTimeDetailsRepository.existsByMentorAndSlotAndDateAndMenteeIsNull(mentor, Slot.SLOT_1, slotDate3)).thenReturn(true);
//        when(mentorAvailableTimeDetailsRepository.existsBySlotAndDateAndMentee(Slot.SLOT_1, slotDate3, mentee)).thenReturn(false);
//
//        boolean[][] result = controller.availableSlotMatrix(mentor, mentee, minDate, maxDate);
//
//        int expectedDays = (int) (maxDate.toEpochDay() - minDate.toEpochDay()) + 1;
//        assertEquals(expectedDays, result[0].length, "number of column in matrix must be equal number of days");
//
//        int dayIndex1 = (int) (slotDate1.toEpochDay() - minDate.toEpochDay());
//        assertTrue(result[Slot.SLOT_3.ordinal()][dayIndex1], "Slot 3 in 1/7 must be available");
//
//        int dayIndex2 = (int) (slotDate2.toEpochDay() - minDate.toEpochDay());
//        assertFalse(result[Slot.SLOT_2.ordinal()][dayIndex2], "Slot 2 in 15/7 must be unavailable (mentee clash)");
//
//        int dayIndex3 = (int) (slotDate3.toEpochDay() - minDate.toEpochDay());
//        assertTrue(result[Slot.SLOT_1.ordinal()][dayIndex3], "Slot 1 in 30/7 must be available");
//
//        for (int i = 0; i < Slot.values().length; i++) {
//            for (int j = 0; j < result[i].length; j++) {
//                if (!((i == Slot.SLOT_3.ordinal() && j == dayIndex1) ||
//                      (i == Slot.SLOT_1.ordinal() && j == dayIndex3))) {
//                    assertFalse(result[i][j], "Slot[" + i + "][" + j + "] must be false");
//                }
//            }
//        }
//    }

}

