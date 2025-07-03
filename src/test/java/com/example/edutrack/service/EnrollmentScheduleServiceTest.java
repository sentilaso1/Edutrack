package com.example.edutrack.service;

import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeDetailsRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.implementation.EnrollmentScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnrollmentScheduleServiceTest {
    private EnrollmentScheduleRepository enrollmentScheduleRepository;
    private MentorAvailableTimeRepository mentorAvailableTimeRepository;
    private MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository;

    private EnrollmentScheduleServiceImpl enrollmentScheduleService;

    @BeforeEach
    void setUp() {
        enrollmentScheduleRepository = Mockito.mock(EnrollmentScheduleRepository.class);
        mentorAvailableTimeRepository = Mockito.mock(MentorAvailableTimeRepository.class);
        mentorAvailableTimeDetailsRepository = Mockito.mock(MentorAvailableTimeDetailsRepository.class);

        enrollmentScheduleService = new EnrollmentScheduleServiceImpl(
                enrollmentScheduleRepository,
                mentorAvailableTimeRepository,
                mentorAvailableTimeDetailsRepository
        );
    }
    //test findStartLearningTime function

    // TC 1.1 sorted multiple date in string to extract
    @Test
    void testFindStartLearningTime_multipleEntries_sortedCorrectly() {
        String summary = "2025-07-30,slot_1; 2025-07-15,slot_5; 2025-07-01,slot_2; 2025-07-01,slot_3";

        List<RequestedSchedule> result = enrollmentScheduleService.findStartLearningTime(summary);

        assertEquals(4, result.size());

        assertEquals(LocalDate.of(2025, 7, 1), result.get(0).getRequestedDate());
        assertEquals(Slot.SLOT_2, result.get(0).getSlot());

        assertEquals(LocalDate.of(2025, 7, 1), result.get(1).getRequestedDate());
        assertEquals(Slot.SLOT_3, result.get(1).getSlot());

        assertEquals(LocalDate.of(2025, 7, 15), result.get(2).getRequestedDate());
        assertEquals(Slot.SLOT_5, result.get(2).getSlot());

        assertEquals(LocalDate.of(2025, 7, 30), result.get(3).getRequestedDate());
        assertEquals(Slot.SLOT_1, result.get(3).getSlot());
    }

    //TC 1.2 single date-slot in string
    @Test
    void testFindStartLearningTime_singleEntry() {
        String summary = "2025-08-10,slot_4";

        List<RequestedSchedule> result = enrollmentScheduleService.findStartLearningTime(summary);

        assertEquals(1, result.size());
        RequestedSchedule schedule = result.get(0);
        assertEquals(LocalDate.of(2025, 8, 10), schedule.getRequestedDate());
        assertEquals(Slot.SLOT_4, schedule.getSlot());
        assertEquals(Day.SUNDAY, schedule.getDay());
    }

    // TC 1.3 empty input
    @Test
    void testFindStartLearningTime_emptyInput_returnsEmptyList() {
        String summary = "";

        List<RequestedSchedule> result = enrollmentScheduleService.findStartLearningTime(summary);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TC 1.4 unsorted list pair date-slot extract from summary string
    @Test
    void testFindStartLearningTime_unsortedDates_triggerBubbleSort() {
        // reverse sorted → ensure sort triggers maximum swaps
        String summary = "2025-12-03,slot_5; 2025-11-01,slot_3; 2025-10-01,slot_1";

        List<RequestedSchedule> result = enrollmentScheduleService.findStartLearningTime(summary);

        assertEquals(3, result.size());
        assertEquals(LocalDate.of(2025, 10, 1), result.get(0).getRequestedDate());
        assertEquals(Slot.SLOT_1, result.get(0).getSlot());

        assertEquals(LocalDate.of(2025, 11, 1), result.get(1).getRequestedDate());
        assertEquals(Slot.SLOT_3, result.get(1).getSlot());

        assertEquals(LocalDate.of(2025, 12, 3), result.get(2).getRequestedDate());
        assertEquals(Slot.SLOT_5, result.get(2).getSlot());
    }


    //sortDayByWeek

    // TC 5.1 empty list days, slots
    @Test
    void testEmptyList() {
        List<Day> days = new ArrayList<>();
        List<Slot> slots = new ArrayList<>();
        enrollmentScheduleService.sortDayByWeek(days, slots);
        assertTrue(days.isEmpty());
        assertTrue(slots.isEmpty());
    }

    // TC 5.2 empty list with 1 element
    @Test
    void testSingleElement() {
        List<Day> days = new ArrayList<>(List.of(Day.MONDAY));
        List<Slot> slots = new ArrayList<>(List.of(Slot.SLOT_1));
        enrollmentScheduleService.sortDayByWeek(days, slots);
        assertEquals(List.of(Day.MONDAY), days);
        assertEquals(List.of(Slot.SLOT_1), slots);
    }

    // TC 5.3 prior day earlier later day
    @Test
    void testDayEarlier_NoSwap() {
        List<Day> days = new ArrayList<>(List.of(Day.MONDAY, Day.TUESDAY));
        List<Slot> slots = new ArrayList<>(List.of(Slot.SLOT_1, Slot.SLOT_1));
        enrollmentScheduleService.sortDayByWeek(days, slots);
        assertEquals(List.of(Day.MONDAY, Day.TUESDAY), days);
        assertEquals(List.of(Slot.SLOT_1, Slot.SLOT_1), slots);
    }

    // TC 5.4 Out-of-order days should trigger a swap
    @Test
    void testDayLater_ShouldSwap() {
        List<Day> days = new ArrayList<>(List.of(Day.WEDNESDAY, Day.MONDAY));
        List<Slot> slots = new ArrayList<>(List.of(Slot.SLOT_3, Slot.SLOT_1));
        enrollmentScheduleService.sortDayByWeek(days, slots);
        assertEquals(List.of(Day.MONDAY, Day.WEDNESDAY), days);
        assertEquals(List.of(Slot.SLOT_1, Slot.SLOT_3), slots);
    }

    // TC 5.5 Same day, correct slot order (no swap expected)
    @Test
    void testSameDaySlotEarlier_NoSwap() {
        List<Day> days = new ArrayList<>(List.of(Day.TUESDAY, Day.TUESDAY));
        List<Slot> slots = new ArrayList<>(List.of(Slot.SLOT_1, Slot.SLOT_3));
        enrollmentScheduleService.sortDayByWeek(days, slots);
        assertEquals(List.of(Day.TUESDAY, Day.TUESDAY), days);
        assertEquals(List.of(Slot.SLOT_1, Slot.SLOT_3), slots);
    }

    // TC 5.6: Same day, slots out of order (should swap)
    @Test
    void testSameDaySlotLater_ShouldSwap() {
        List<Day> days = new ArrayList<>(List.of(Day.THURSDAY, Day.THURSDAY));
        List<Slot> slots = new ArrayList<>(List.of(Slot.SLOT_5, Slot.SLOT_2));
        enrollmentScheduleService.sortDayByWeek(days, slots);
        assertEquals(List.of(Day.THURSDAY, Day.THURSDAY), days);
        assertEquals(List.of(Slot.SLOT_2, Slot.SLOT_5), slots);
    }

    // TC 5.7: Complex sequence requiring multiple swaps
    @Test
    void testMultipleSwapChains() {
        List<Day> days = new ArrayList<>(List.of(Day.WEDNESDAY, Day.TUESDAY, Day.MONDAY, Day.TUESDAY));
        List<Slot> slots = new ArrayList<>(List.of(Slot.SLOT_1, Slot.SLOT_3, Slot.SLOT_2, Slot.SLOT_1));

        enrollmentScheduleService.sortDayByWeek(days, slots);

        assertEquals(List.of(Day.MONDAY, Day.TUESDAY, Day.TUESDAY, Day.WEDNESDAY), days);
        assertEquals(List.of(Slot.SLOT_2, Slot.SLOT_1, Slot.SLOT_3, Slot.SLOT_1), slots);
    }

}
