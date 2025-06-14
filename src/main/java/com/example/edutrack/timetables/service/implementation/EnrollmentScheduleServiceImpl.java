package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EnrollmentScheduleServiceImpl implements EnrollmentScheduleService {
    private final EnrollmentScheduleRepository enrollmentScheduleRepository;
    private final MentorAvailableTimeRepository mentorAvailableTimeRepository;

    public EnrollmentScheduleServiceImpl(EnrollmentScheduleRepository enrollmentScheduleRepository, MentorAvailableTimeRepository mentorAvailableTimeRepository) {
        this.enrollmentScheduleRepository = enrollmentScheduleRepository;
        this.mentorAvailableTimeRepository = mentorAvailableTimeRepository;
    }

    private void sortDayByWeek(List<Day> days, List<Slot> slots) {
        List<String> weekDays = new ArrayList<>();
        weekDays.add("MONDAY");
        weekDays.add("TUESDAY");
        weekDays.add("WEDNESDAY");
        weekDays.add("THURSDAY");
        weekDays.add("FRIDAY");
        weekDays.add("SATURDAY");
        weekDays.add("SUNDAY");

        for (int i = 0; i < days.size(); i++) {
            for (int j = i + 1; j < days.size(); j++) {
                if (weekDays.indexOf(days.get(i).name()) > weekDays.indexOf(days.get(j).name())) {
                    Day t = days.get(i);
                    days.set(i, days.get(j));
                    days.set(j, t);

                    Slot s = slots.get(i);
                    slots.set(i, slots.get(j));
                    slots.set(j, s);
                }
            }
        }
    }

    @Override
    public String findStartLearningTime(Mentee user,
                                        CourseMentor courseMentor,
                                        List<Slot> slot,
                                        List<Day> day,
                                        Integer totalSlot) {

        LocalDate startDate = LocalDate.now();
        sortDayByWeek(day, slot);

        while (!day.contains(Day.valueOf(startDate.getDayOfWeek().name()))) {
            startDate = startDate.plusDays(1);
        }
        System.out.println("INITIAL: " + startDate);

        int found = 0;
        int i = day.indexOf(Day.valueOf(startDate.getDayOfWeek().name()));
        String result = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (found < totalSlot) {
            Slot currentSlot = slot.get(i);
            Day currentDay = day.get(i);
            System.out.println("=============================================================");
            System.out.println(startDate);
            System.out.println(currentDay.name() + "-" + currentSlot.name());

            boolean isMentorAvailableTime = mentorAvailableTimeRepository.isMentorAvailableTime(courseMentor.getMentor(), currentSlot, startDate);
            if (isMentorAvailableTime) {
                return null;
            }
            LocalDate minDate = mentorAvailableTimeRepository.findMinStartDate(courseMentor.getMentor());
            LocalDate maxDate = mentorAvailableTimeRepository.findMaxEndDate(courseMentor.getMentor());
            if (minDate == null && maxDate == null) {
                return null;
            } else {
                if (startDate.isBefore(minDate) || startDate.isAfter(maxDate)) {
                    return null;
                }
            }

            boolean clashMentor = enrollmentScheduleRepository
                    .isTakenSlot(courseMentor.getMentor(), currentSlot, currentDay, startDate);
            boolean clashMentee = enrollmentScheduleRepository
                    .isLearningSlot(user, currentSlot, currentDay, startDate);

            if (!clashMentor && !clashMentee) {
                if (found == 0) {
                    result = startDate.format(formatter) + "-" + currentSlot.name();
                    System.out.println("FOUND: " + result);
                }
                found++;
            } else {
                found = 0;
                result = null;
            }

            i++;
            if (i == day.size()) {
                i = 0;
                startDate = startDate.plusDays(1);
                while (!day.contains(Day.valueOf(startDate.getDayOfWeek().name()))) {
                    startDate = startDate.plusDays(1);
                }
            } else if (!day.get(i).equals(day.get(i - 1))) {
                startDate = startDate.plusDays(1);
                while (!day.contains(Day.valueOf(startDate.getDayOfWeek().name()))) {
                    startDate = startDate.plusDays(1);
                }
            }
        }
        return result;
    }

    @Override
    public List<EnrollmentSchedule> findAll() {
        return enrollmentScheduleRepository.findAll();
    }

    @Override
    public List<EnrollmentSchedule> findByMentorAndDateBetween(Mentor mentor, LocalDate weekStart, LocalDate weekEnd) {
        return enrollmentScheduleRepository.findByMentorAndDateBetween(mentor, weekStart, weekEnd);
    }

    @Override
    public EnrollmentSchedule findById(Integer esid) {
        return enrollmentScheduleRepository.findById(esid).get();
    }

}
