package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.*;
import com.example.edutrack.timetables.repository.MentorAvailableTimeDetailsRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MentorAvailableTimeServiceImpl implements MentorAvailableTimeService {

    private final EnrollmentScheduleServiceImpl enrollmentScheduleServiceImpl;
    MentorAvailableTimeRepository mentorAvailableTimeRepository;
    MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository;


    public MentorAvailableTimeServiceImpl(MentorAvailableTimeRepository scheduleRepository, EnrollmentScheduleServiceImpl enrollmentScheduleServiceImpl, MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository) {
        this.mentorAvailableTimeRepository = scheduleRepository;
        this.enrollmentScheduleServiceImpl = enrollmentScheduleServiceImpl;
        this.mentorAvailableTimeDetailsRepository = mentorAvailableTimeDetailsRepository;
    }

    public List<MentorAvailableTimeDetails> findByMentor(Mentor mentor){
        return mentorAvailableTimeDetailsRepository.findByMentor(mentor);
    }

    @Override
    public void insertWorkingSchedule(List<MentorAvailableTime> schedule) {
        mentorAvailableTimeRepository.saveAll(schedule);
    }

    @Override
    public List<MentorAvailableTime> findByMentorId(Mentor mentor) {
        return mentorAvailableTimeRepository.findByMentorId(mentor.getId());
    }

    @Override
    public String alertValidStartEndTime(LocalDate start, LocalDate end, Mentor mentor) {
        List<MentorAvailableTime> a = mentorAvailableTimeRepository.findMentorAvailableTimeByStatus(end,mentor, MentorAvailableTime.Status.DRAFT, MentorAvailableTime.Status.REJECTED);
        for(MentorAvailableTime b : a) {
            System.out.print(b.getId().getEndDate() + " ");
            System.out.print(b.getId().getStartDate() + " ");
            System.out.println(b.getStatus());
        }

        if(start.isAfter(end)) {
            return "Start time cannot be after end time";
        }
        if(end.isBefore(LocalDate.now())) {
            return "End time cannot be before now";
        }
        if(mentorAvailableTimeRepository.isEndDateExisted(end,mentor, MentorAvailableTime.Status.DRAFT, MentorAvailableTime.Status.REJECTED)) {
            return "Time Range has been registered";
        }
        return null;
    }

    @Override
    public List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(Mentor mentor, MentorAvailableTime.Status status) {
        return mentorAvailableTimeRepository.findAllDistinctStartEndDates(mentor, status);
    }

    @Override
    public List<MentorAvailableSlotDTO> findAllSlotByEndDate(Mentor mentor, LocalDate endDate) {
        return mentorAvailableTimeRepository.findAllSlotByEndDate(mentor, endDate);
    }

    @Override
    public LocalDate findMaxEndDate(Mentor mentor) {
        return mentorAvailableTimeRepository.findMaxEndDate(mentor);
    }

    @Override
    public LocalDate findMinStartDate(Mentor mentor) {
        return mentorAvailableTimeRepository.findMinStartDate(mentor);
    }

    @Override
    public List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(MentorAvailableTime.Status status) {
        return mentorAvailableTimeRepository.findAllDistinctStartEndDates(status);
    }

    @Override
    public boolean[][] slotDayMatrix(List<MentorAvailableSlotDTO> setSlots){
        boolean[][] slotDayMatrix = new boolean[Slot.values().length][Day.values().length];

        for (MentorAvailableSlotDTO dto : setSlots) {
            int slotIndex = dto.getSlot().ordinal();
            int dayIndex = dto.getDay().ordinal();
            slotDayMatrix[slotIndex][dayIndex] = true;
        }
        return slotDayMatrix;
    }

    @Override
    public List<MentorAvailableTime> findAllMentorAvailableTimeByEndDate(Mentor mentor, LocalDate endDate) {
        return mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(mentor, endDate);
    }

    @Override
    public LocalDate findMaxEndDateByStatus(Mentor mentor, MentorAvailableTime.Status enumValue) {
        return mentorAvailableTimeRepository.findMaxEndDateByStatus(mentor, enumValue);
    }

    @Override
    public void insertMentorAvailableTime(LocalDate startDate, LocalDate endDate, Mentor mentor){
        List<MentorAvailableTime> mentorAvailableTimes = findAllMentorAvailableTimeByEndDate(mentor, endDate);
        if(mentorAvailableTimes.isEmpty()) {
            return;
        }
        List<Slot> slots = mentorAvailableTimes.stream().map(mat -> mat.getId().getSlot()).collect(Collectors.toList());
        List<Day> days = mentorAvailableTimes.stream().map(mat -> mat.getId().getDay()).collect(Collectors.toList());

        LocalDate currentTime = LocalDate.now();
        if(startDate.isAfter(currentTime)) {
            currentTime = startDate;
        }
        enrollmentScheduleServiceImpl.sortDayByWeek(days, slots);

        while (!days.contains(Day.valueOf(currentTime.getDayOfWeek().name()))) {
            currentTime = currentTime.plusDays(1);
        }

        int i = days.indexOf(Day.valueOf(currentTime.getDayOfWeek().name()));

        while(currentTime.isBefore(endDate)) {
            Slot currentSlot = slots.get(i);

            MentorAvailableTimeDetails schedule = new MentorAvailableTimeDetails();
            schedule.setMentor(mentor);
            schedule.setSlot(currentSlot);
            schedule.setDate(currentTime);
            mentorAvailableTimeDetailsRepository.save(schedule);

            i++;
            if (i == days.size()) {
                i = 0;
                currentTime = currentTime.plusDays(1);
                while (!days.contains(Day.valueOf(currentTime.getDayOfWeek().name()))) {
                    currentTime = currentTime.plusDays(1);
                }
            } else if (!days.get(i).equals(days.get(i - 1))) {
                currentTime = currentTime.plusDays(1);
                while (!days.contains(Day.valueOf(currentTime.getDayOfWeek().name()))) {
                    currentTime = currentTime.plusDays(1);
                }
            }
        }
    }

    @Override
    public List<MentorAvailableTimeDetails> findByMentorIdAndStatusAndDateRange(
            UUID mentorId,
            LocalDate startDate,
            LocalDate endDate
    ){
        return  mentorAvailableTimeRepository.findByMentorIdAndStatusAndDateRange(mentorId, startDate, endDate);
    }

    @Override
    public List<MentorAvailableSlotDTO> findOnlyApprovedSlotsByEndDate(Mentor mentor, LocalDate endDate) {
        return mentorAvailableTimeRepository.findApprovedSlotsByEndDate(mentor, endDate, MentorAvailableTime.Status.APPROVED);
    }


}
