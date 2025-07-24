package com.example.edutrack.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.MentorAvailableTimeDetails;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.MentorAvailableTimeDetailsRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.implementation.EnrollmentScheduleServiceImpl;
import com.example.edutrack.timetables.service.implementation.MentorAvailableTimeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MentorAvailableTimeServiceTest {
    private MentorAvailableTimeRepository mentorAvailableTimeRepository;
    private EnrollmentScheduleServiceImpl enrollmentScheduleServiceImpl;
    private MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository;

    private MentorAvailableTimeServiceImpl mentorAvailableTimeService;

    @BeforeEach
    void setUp() {
        mentorAvailableTimeRepository = Mockito.mock(MentorAvailableTimeRepository.class);
        enrollmentScheduleServiceImpl = Mockito.mock(EnrollmentScheduleServiceImpl.class);
        mentorAvailableTimeDetailsRepository = Mockito.mock(MentorAvailableTimeDetailsRepository.class);

        mentorAvailableTimeService = new MentorAvailableTimeServiceImpl(
                mentorAvailableTimeRepository,
                enrollmentScheduleServiceImpl,
                mentorAvailableTimeDetailsRepository
        );
    }

    // TC 4.1 insert mentor available time with valid multiple slot
    @Test
    void testInsertMentorAvailableTimes_withMultipleSlots_shouldGenerateCorrectSchedules() {
        Mentor mentor = new Mentor();
        mentor.setId(UUID.randomUUID());

        LocalDate startDate = LocalDate.of(2025, 8, 1);  // Friday
        LocalDate endDate = LocalDate.of(2025, 8, 15);   // Next Friday

        // Available on Tuesday (SLOT_1) and Thursday (SLOT_3)
        MentorAvailableTime mat1 = new MentorAvailableTime(mentor, Slot.SLOT_1, Day.TUESDAY, startDate, endDate);
        MentorAvailableTime mat2 = new MentorAvailableTime(mentor, Slot.SLOT_3, Day.THURSDAY, startDate, endDate);

        when(mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(eq(mentor), eq(endDate)))
                .thenReturn(List.of(mat1, mat2));

        mentorAvailableTimeService.insertMentorAvailableTime(startDate, endDate, mentor);

        ArgumentCaptor<MentorAvailableTimeDetails> captor = ArgumentCaptor.forClass(MentorAvailableTimeDetails.class);
        verify(mentorAvailableTimeDetailsRepository, atLeastOnce()).save(captor.capture());

        List<MentorAvailableTimeDetails> saved = captor.getAllValues();
        assertFalse(saved.isEmpty());

        for (MentorAvailableTimeDetails detail : saved) {
            assertEquals(mentor, detail.getMentor());
            assertFalse(detail.getDate().isBefore(startDate));
            assertTrue(detail.getDate().isBefore(endDate));
            DayOfWeek day = detail.getDate().getDayOfWeek();
            assertTrue(day == DayOfWeek.TUESDAY || day == DayOfWeek.THURSDAY);
            assertTrue(detail.getSlot() == Slot.SLOT_1 || detail.getSlot() == Slot.SLOT_3);
        }
    }

    // TC 4.2 start date is today
    @Test
    void testInsertMentorAvailableTimes_startDateIsToday() {
        Mentor mentor = new Mentor();
        mentor.setId(UUID.randomUUID());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);

        MentorAvailableTime mat = new MentorAvailableTime(mentor, Slot.SLOT_1, Day.valueOf(today.getDayOfWeek().name()), today, endDate);

        when(mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(mentor, endDate))
                .thenReturn(List.of(mat));

        mentorAvailableTimeService.insertMentorAvailableTime(today, endDate, mentor);

        verify(mentorAvailableTimeDetailsRepository, atLeastOnce()).save(any());
    }

    // TC 4.3 empty found list
    @Test
    void testInsertMentorAvailableTimes_emptyAvailabilityList_shouldNotSave() {
        Mentor mentor = new Mentor();
        mentor.setId(UUID.randomUUID());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);

        when(mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(mentor, endDate))
                .thenReturn(Collections.emptyList());

        mentorAvailableTimeService.insertMentorAvailableTime(startDate, endDate, mentor);

        verify(mentorAvailableTimeDetailsRepository, never()).save(any());
    }


    // TC 4.4 just 1 valid day
    @Test
    void testInsertMentorAvailableTimes_oneValidDay_shouldInsertOnce() {
        Mentor mentor = new Mentor();
        mentor.setId(UUID.randomUUID());

        LocalDate start = LocalDate.of(2025, 7, 28); // MONDAY
        LocalDate end = start.plusDays(1); // only 1 day

        MentorAvailableTime mat = new MentorAvailableTime(mentor, Slot.SLOT_4, Day.MONDAY, start, end);

        when(mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(mentor, end))
                .thenReturn(List.of(mat));

        mentorAvailableTimeService.insertMentorAvailableTime(start, end, mentor);

        ArgumentCaptor<MentorAvailableTimeDetails> captor = ArgumentCaptor.forClass(MentorAvailableTimeDetails.class);
        verify(mentorAvailableTimeDetailsRepository).save(captor.capture());

        MentorAvailableTimeDetails inserted = captor.getValue();
        assertEquals(start, inserted.getDate());
        assertEquals(Slot.SLOT_4, inserted.getSlot());
        assertEquals(mentor, inserted.getMentor());
    }

    // TC 4.5 valid found list to insert
    @Test
    void testInsertMentorAvailableTimes_fullLoopWithWraparound() {
        Mentor mentor = new Mentor();
        mentor.setId(UUID.randomUUID());

        LocalDate start = LocalDate.of(2025, 8, 1); // Friday
        LocalDate end = LocalDate.of(2025, 8, 10); // Sunday

        MentorAvailableTime mat1 = new MentorAvailableTime(mentor, Slot.SLOT_2, Day.FRIDAY, start, end);
        MentorAvailableTime mat2 = new MentorAvailableTime(mentor, Slot.SLOT_1, Day.MONDAY, start, end);

        when(mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(mentor, end))
                .thenReturn(List.of(mat1, mat2));

        mentorAvailableTimeService.insertMentorAvailableTime(start, end, mentor);

        verify(mentorAvailableTimeDetailsRepository, atLeastOnce()).save(any());
    }



}



