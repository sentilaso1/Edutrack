package com.example.edutrack.service;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.GoalRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.implementation.DashboardServiceImpl;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MenteeDashboardServiceImplTest {

    private EnrollmentScheduleRepository enrollmentScheduleRepository;
    private MentorRepository mentorRepository;
    private EnrollmentRepository enrollmentRepository;
    private TagRepository tagRepository;
    private GoalRepository goalRepository;

    private DashboardServiceImpl dashboardService;

    private UUID menteeId;

    @BeforeEach
    void setUp() {
        enrollmentScheduleRepository = mock(EnrollmentScheduleRepository.class);
        mentorRepository = mock(MentorRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);
        tagRepository = mock(TagRepository.class);
        goalRepository = mock(GoalRepository.class);

        dashboardService = new DashboardServiceImpl(
                enrollmentScheduleRepository,
                mentorRepository,
                enrollmentRepository,
                tagRepository,
                goalRepository
        );

        menteeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should return 'No upcoming session' when list is empty")
    void testEmptyScheduleList() {
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(Collections.emptyList());

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    @Test
    @DisplayName("Should return 'No upcoming session' when all schedules are in the past")
    void testAllSchedulesInPast() {
        EnrollmentSchedule past = mockSchedule(LocalDate.now().minusDays(1), LocalTime.of(9, 0), "Java");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(past));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    @Test
    @DisplayName("Should return one upcoming session correctly")
    void testOneUpcomingSchedule() {
        EnrollmentSchedule future = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(10, 0), "Python");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(future));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Python");
    }

    @Test
    @DisplayName("Should return earliest upcoming session when multiple exist")
    void testMultipleUpcomingSchedules() {
        EnrollmentSchedule future1 = mockSchedule(LocalDate.now().plusDays(2), LocalTime.of(11, 0), "Java");
        EnrollmentSchedule future2 = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(8, 0), "Python");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(future1, future2));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Python");
    }

    @Test
    @DisplayName("Should handle mix of past and future schedules")
    void testMixedSchedules() {
        EnrollmentSchedule past = mockSchedule(LocalDate.now().minusDays(1), LocalTime.of(10, 0), "C++");
        EnrollmentSchedule future = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(9, 0), "Java");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(past, future));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Java");
    }

    @Test
    @DisplayName("Should handle exact current datetime as past")
    void testScheduleAtCurrentTime() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        EnrollmentSchedule nowSchedule = mockSchedule(today, now.minusMinutes(1), "NodeJS");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(nowSchedule));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    @Test
    @DisplayName("Should return correct format of date")
    void testFormattedOutput() {
        EnrollmentSchedule future = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(10, 30), "Rust");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(future));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Rust - ");
    }

    @Test
    @DisplayName("Should ignore null values safely")
    void testNullHandling() {
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(null);

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    private EnrollmentSchedule mockSchedule(LocalDate date, LocalTime time, String courseName) {
        EnrollmentSchedule schedule = mock(EnrollmentSchedule.class);
        Slot slot = mock(Slot.class);
        when(slot.getStartTime()).thenReturn(time);

        Course course = mock(Course.class);
        when(course.getName()).thenReturn(courseName);

        CourseMentor courseMentor = mock(CourseMentor.class);
        when(courseMentor.getCourse()).thenReturn(course);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getCourseMentor()).thenReturn(courseMentor);

        when(schedule.getSlot()).thenReturn(slot);
        when(schedule.getDate()).thenReturn(date);
        when(schedule.getEnrollment()).thenReturn(enrollment);

        return schedule;
    }
}