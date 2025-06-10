package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
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

    public EnrollmentScheduleServiceImpl(EnrollmentScheduleRepository enrollmentScheduleRepository) {
        this.enrollmentScheduleRepository = enrollmentScheduleRepository;
    }

//    @Override
//    public String findStartLearningTime(Mentee user,
//                                        CourseMentor courseMentor,
//                                        List<Slot> slotList,
//                                        List<Day> dayList,
//                                        Integer totalSlot) {
//
//        int found = 0;
//        LocalDate date = LocalDate.now().plusDays(1);
//        String firstDate = null;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        // Tìm ngày đầu tiên có trong dayList
//        while (!dayList.contains(Day.valueOf(date.getDayOfWeek().name()))) {
//            date = date.plusDays(1);
//        }
//
//        int startIdx = dayList.indexOf(Day.valueOf(date.getDayOfWeek().name()));
//
//        while (found < totalSlot) {
//            int patternIdx = (startIdx + found) % dayList.size();
//            Day expectedDay = dayList.get(patternIdx);
//            Slot expectedSlot = slotList.get(patternIdx);
//
//            // Tìm ngày có thứ expectedDay
//            while (!Day.valueOf(date.getDayOfWeek().name()).equals(expectedDay)) {
//                date = date.plusDays(1);
//            }
//
//            // Tìm các slot khả dụng cho ngày này
//            List<Slot> availableSlots = new ArrayList<>();
//            for (int i = 0; i < dayList.size(); i++) {
//                if (dayList.get(i).equals(expectedDay)) {
//                    availableSlots.add(slotList.get(i));
//                }
//            }
//
//            // Thử từng slot
//            Slot selectedSlot = null;
//            for (Slot slot : availableSlots) {
//                if (!enrollmentScheduleRepository.isTakenSlot(courseMentor, slot, expectedDay, date) &&
//                    !enrollmentScheduleRepository.isLearningSlot(user, slot, expectedDay, date)) {
//                    selectedSlot = slot;
//                    break;
//                }
//            }
//
//            if (selectedSlot != null) {
//                if (found == 0) {
//                    firstDate = date.format(formatter) + "-" + selectedSlot.name();
//                }
//                found++;
//                date = date.plusDays(1);
//            } else {
//                // Reset và thử ngày tiếp theo
//                found = 0;
//                firstDate = null;
//                date = date.plusDays(1);
//                while (!dayList.contains(Day.valueOf(date.getDayOfWeek().name()))) {
//                    date = date.plusDays(1);
//                }
//                startIdx = dayList.indexOf(Day.valueOf(date.getDayOfWeek().name()));
//            }
//        }
//
//        return firstDate;
//    }

    @Override
    public String findStartLearningTime(Mentee user,
                                        CourseMentor courseMentor,
                                        List<Slot> slot,
                                        List<Day> day,
                                        Integer totalSlot) {

        LocalDate startDate = LocalDate.now();

        while (!day.contains(Day.valueOf(startDate.getDayOfWeek().name()))) {
            startDate = startDate.plusDays(1);
        }

        int found = 0;
        int i = day.indexOf(Day.valueOf(startDate.getDayOfWeek().name()));
        String result = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (found < totalSlot) {
            Slot currentSlot = slot.get(i);
            Day currentDay = day.get(i);

            boolean clashMentor = enrollmentScheduleRepository
                    .isTakenSlot(courseMentor, currentSlot, currentDay, startDate);
            boolean clashMentee = enrollmentScheduleRepository
                    .isLearningSlot(user, currentSlot, currentDay, startDate);

            if (!clashMentor && !clashMentee) {
                if (found == 0) {
                    result = startDate.format(formatter) + "-" + currentSlot.name();
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

}
