package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.common.service.implementations.EmailService;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.timetables.dto.*;
import com.example.edutrack.timetables.model.*;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeDetailsRepository;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EnrollmentScheduleServiceImpl implements EnrollmentScheduleService {
    private final EnrollmentScheduleRepository enrollmentScheduleRepository;
    private final MentorAvailableTimeRepository mentorAvailableTimeRepository;
    private final MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository;
    private final EmailService emailService;
    private final MenteeRepository menteeRepository;
    private final CourseMentorRepository courseMentorRepository;


    public EnrollmentScheduleServiceImpl(EnrollmentScheduleRepository enrollmentScheduleRepository, MentorAvailableTimeRepository mentorAvailableTimeRepository, MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository, MenteeRepository menteeRepository, CourseMentorRepository courseMentorRepository, EmailService emailService) {
        this.enrollmentScheduleRepository = enrollmentScheduleRepository;
        this.mentorAvailableTimeRepository = mentorAvailableTimeRepository;
        this.mentorAvailableTimeDetailsRepository = mentorAvailableTimeDetailsRepository;
        this.emailService = emailService;
        this.menteeRepository = menteeRepository;
        this.courseMentorRepository = courseMentorRepository;
    }

    public void sortDayByWeek(List<Day> days, List<Slot> slots) {
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
                } else if (weekDays.indexOf(days.get(i).name()) == weekDays.indexOf(days.get(j).name())) {
                    if (slots.get(i).ordinal() > slots.get(j).ordinal()) {
                        Slot s = slots.get(i);
                        slots.set(i, slots.get(j));
                        slots.set(j, s);
                    }
                }
            }
        }
    }

    @Override
    public List<RequestedSchedule> findStartLearningTime(String summary) {
        if (summary == null || summary.isEmpty()) {
            return new ArrayList<>();
        }
        List<Slot> slot = new ArrayList<>();
        List<LocalDate> date = new ArrayList<>();
        parseDaySlotString(summary, date, slot);
        List<RequestedSchedule> requestedSchedules = new ArrayList<>();
        for (int i = 0; i < date.size(); i++) {
            for (int j = date.size() - 1; j > i; j--) {
                if (date.get(j).isBefore(date.get(j - 1))) {
                    LocalDate tempDate = date.get(j);
                    date.set(j, date.get(j - 1));
                    date.set(j - 1, tempDate);

                    Slot tempSlot = slot.get(j);
                    slot.set(j, slot.get(j - 1));
                    slot.set(j - 1, tempSlot);
                }
            }
            requestedSchedules.add(new RequestedSchedule(slot.get(i), Day.valueOf(date.get(i).getDayOfWeek().name()), date.get(i)));
        }
        return requestedSchedules;
    }

    public static void parseDaySlotString(String input,
                                          List<LocalDate> dates,
                                          List<Slot> slots) {
        String[] pairs = input.split(";");

        for (String pair : pairs) {
            String[] parts = pair.trim().split(",");

            if (parts.length == 2) {
                LocalDate date = LocalDate.parse(parts[0].trim());
                Slot slot = Slot.valueOf(parts[1].trim().toUpperCase());

                dates.add(date);
                slots.add(slot);
            } else {
                throw new IllegalArgumentException("Invalid format: " + pair);
            }
        }
    }


    @Override
    public void saveEnrollmentSchedule(Enrollment enrollment) {
        String summary = enrollment.getScheduleSummary();
        List<RequestedSchedule> requestedSchedules = findStartLearningTime(summary);

        for (RequestedSchedule requestedSchedule : requestedSchedules) {
            enrollmentScheduleRepository.save(new EnrollmentSchedule(enrollment, requestedSchedule.getSlot(), requestedSchedule.getRequestedDate()));
        }

        List<MentorAvailableTimeDetails> mentorAvailableTimeDetails = mentorAvailableTimeDetailsRepository.findByMentor(enrollment.getCourseMentor().getMentor());
        for (MentorAvailableTimeDetails mentorAvailableTimeDetail : mentorAvailableTimeDetails) {
            for (RequestedSchedule requestedSchedule : requestedSchedules) {
                if (mentorAvailableTimeDetail.getDate().equals(requestedSchedule.getRequestedDate()) && requestedSchedule.getSlot().equals(mentorAvailableTimeDetail.getSlot())) {
                    mentorAvailableTimeDetail.setMentee(enrollment.getMentee());
                    mentorAvailableTimeDetailsRepository.save(mentorAvailableTimeDetail);
                }
            }
        }
    }

    @Override
    public List<EnrollmentSchedule> findEnrollmentScheduleByEnrollment(Enrollment enrollment) {
        return enrollmentScheduleRepository.findEnrollmentScheduleByEnrollment(enrollment);
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

    @Override
    public EnrollmentSchedule findById(int enrollmentScheduleId) {
        return enrollmentScheduleRepository.findById(enrollmentScheduleId).orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentScheduleId));
    }

    @Override
    public void save(EnrollmentSchedule schedule) {
        enrollmentScheduleRepository.save(schedule);
    }

    @Override
    public Map<String, Integer> getWeeklyStats(UUID menteeId) {
        List<EnrollmentSchedule> schedules = enrollmentScheduleRepository.findAllByMenteeId(menteeId);
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

        int classCount = 0;

        for (EnrollmentSchedule s : schedules) {
            if (!s.getDate().isBefore(startOfWeek) && !s.getDate().isAfter(endOfWeek)) {
                classCount++;
            }
        }

        Map<String, Integer> stats = new HashMap<>();
        stats.put("classes", classCount);
        return stats;
    }


    @Override
    public List<EnrollmentSchedule> getEnrollmentSchedulesByCourseAndMentee(UUID courseMentorId, UUID menteeId) {
        return enrollmentScheduleRepository.findEnrollmentScheduleByMenteeAndCourseMentor(menteeId, courseMentorId);
    }

    @Override
    public List<EnrollmentSchedule> getEnrollmentSchedulesByMentee(UUID menteeId) {
        return enrollmentScheduleRepository.findAllByMenteeId(menteeId);
    }


    @Override
    public int countTestSlot(UUID menteeId) {
        return enrollmentScheduleRepository.countTestSlotsByEnrollment(menteeId);
    }

    @Override
    public List<ScheduleDTO> getScheduleDTOs(List<EnrollmentSchedule> schedules, UUID menteeId) {
        Map<Enrollment, Long> rescheduleCounts = new HashMap<>();
        for (EnrollmentSchedule s : schedules) {
            rescheduleCounts.computeIfAbsent(s.getEnrollment(), e ->
                    enrollmentScheduleRepository.countByEnrollmentAndRescheduleStatusNot(e, EnrollmentSchedule.RescheduleStatus.NONE)
            );
        }
        return schedules.stream().map(schedule -> {
            Slot slot = schedule.getSlot();
            LocalTime start = slot.getStartTime();
            LocalTime end = slot.getEndTime();
            ScheduleDTO dto = new ScheduleDTO();
            dto.setId(schedule.getId());
            dto.setSlot(slot.name());
            dto.setDay(schedule.getDate().getDayOfWeek().name());
            dto.setCourseName(schedule.getEnrollment().getCourseMentor().getCourse().getName());
            dto.setMentorName(schedule.getEnrollment().getCourseMentor().getMentor().getFullName());
            dto.setAttendance(schedule.getAttendance());
            dto.setStartTime(start.toString());
            dto.setEndTime(end.toString());
            dto.setStartHour(start.getHour());
            dto.setStartMinute(start.getMinute());
            dto.setTitle(schedule.getTitleSection());
            dto.setHasTest(schedule.getTest() != null && schedule.getTest());
            dto.setDurationInMinutes((int) ChronoUnit.MINUTES.between(start, end));
            long count = rescheduleCounts.get(schedule.getEnrollment());
            dto.setRescheduleCount((int) count);
            boolean canReschedule = count < 2
                    && schedule.getAttendance() == EnrollmentSchedule.Attendance.NOT_YET
                    && schedule.getRescheduleStatus() == EnrollmentSchedule.RescheduleStatus.NONE
                    && schedule.getDate().isAfter(LocalDate.now());

            dto.setCanReschedule(canReschedule);
            dto.setAvailable(schedule.isAvailable());
            dto.setRescheduleStatus(schedule.getRescheduleStatus());
            dto.setDate(schedule.getDate());
            return dto;
        }).toList();
    }

    @Override
    public boolean submitRescheduleRequest(int scheduleId, Slot newSlot, LocalDate newDate,
                                           String reason, UUID menteeId) {
        try {
            // Find the schedule
            Optional<EnrollmentSchedule> optionalSchedule = enrollmentScheduleRepository.findById(scheduleId);
            if (optionalSchedule.isEmpty()) {
                return false;
            }

            EnrollmentSchedule schedule = optionalSchedule.get();
            Enrollment enrollment = schedule.getEnrollment();
            long rescheduleCount = enrollmentScheduleRepository.countByEnrollmentAndRescheduleStatusNot(
                    enrollment,
                    EnrollmentSchedule.RescheduleStatus.NONE
            );

            if (rescheduleCount >= 2) {
                return false;
            }
            if (!schedule.getEnrollment().getMentee().getId().equals(menteeId)) {
                return false;
            }
            if (!schedule.canRequestReschedule()) {
                return false;
            }

            schedule.setRescheduleStatus(EnrollmentSchedule.RescheduleStatus.REQUESTED);
            schedule.setRequestedNewSlot(newSlot);
            schedule.setRequestedNewDate(newDate);
            schedule.setRescheduleReason(reason);
            schedule.setRescheduleRequestDate(LocalDate.now());
            enrollmentScheduleRepository.save(schedule);
            sendRescheduleNotificationToMentor(schedule);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public ScheduleDTO getScheduleDTO(Long scheduleId, UUID menteeId) {
        Optional<EnrollmentSchedule> optSchedule = enrollmentScheduleRepository.findById(scheduleId.intValue());
        if (optSchedule.isEmpty()) return null;

        EnrollmentSchedule schedule = optSchedule.get();
        if (!schedule.getEnrollment().getMentee().getId().equals(menteeId)) return null;

        return mapToScheduleDTO(schedule, menteeId);
    }

    @Override
    public List<ScheduleDTO> getOccupiedSlotsForWeek(UUID menteeId, LocalDate weekStart, LocalDate weekEnd) {
        List<EnrollmentSchedule> schedules = enrollmentScheduleRepository.findAllByMenteeId(menteeId).stream()
                .filter(s -> !s.getDate().isBefore(weekStart) && !s.getDate().isAfter(weekEnd))
                .toList();

        return getScheduleDTOs(schedules, menteeId);
    }

    @Override
    public boolean isSlotAvailable(UUID menteeId, Slot newSlot, LocalDate newDate, int scheduleId) {
        return enrollmentScheduleRepository.findAllByMenteeId(menteeId).stream()
                .filter(s -> s.getId() != scheduleId && s.isAvailable())
                .noneMatch(s -> s.getSlot() == newSlot && s.getDate().equals(newDate));
    }

    private ScheduleDTO mapToScheduleDTO(EnrollmentSchedule schedule, UUID menteeId) {
        Slot slot = schedule.getSlot();
        LocalTime start = slot.getStartTime();
        LocalTime end = slot.getEndTime();

        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setSlot(slot.name());
        dto.setDay(schedule.getDate().getDayOfWeek().name());
        dto.setDate(schedule.getDate());
        dto.setCourseName(schedule.getEnrollment().getCourseMentor().getCourse().getName());
        dto.setMentorName(schedule.getEnrollment().getCourseMentor().getMentor().getFullName());
        dto.setStartTime(start.toString());
        dto.setMentorId(schedule.getEnrollment().getCourseMentor().getMentor().getId());
        dto.setEndTime(end.toString());
        dto.setStartHour(start.getHour());
        dto.setTitle(schedule.getTitleSection());
        dto.setStartMinute(start.getMinute());
        dto.setAttendance(schedule.getAttendance());
        dto.setDurationInMinutes((int) ChronoUnit.MINUTES.between(start, end));
        dto.setEndHour(start.getHour() + (int) Math.ceil((double) ChronoUnit.MINUTES.between(start, end) / 60.0));
        dto.setHasTest(schedule.getTest() != null && schedule.getTest());
        dto.setDate(schedule.getDate());
        dto.setAvailable(schedule.isAvailable());
        dto.setCanReschedule(schedule.canRequestReschedule());
        dto.setRescheduleStatus(schedule.getRescheduleStatus());

        return dto;
    }

    @Override
    public List<EnrollmentSchedule> getSlotsUnderReview(UUID menteeId, LocalDate startDate, LocalDate endDate) {
        return enrollmentScheduleRepository
                .findByEnrollment_MenteeIdAndRescheduleStatusAndRequestedNewDateBetween(
                        menteeId,
                        EnrollmentSchedule.RescheduleStatus.REQUESTED,
                        startDate,
                        endDate
                );
    }

    @Override
    public List<EnrollmentSchedule> getSlotsUnderReviewByCourse(UUID menteeId, UUID courseMentorId, LocalDate startDate, LocalDate endDate) {
        return enrollmentScheduleRepository.findReviewingSlotsByCourse(menteeId, courseMentorId, startDate, endDate);
    }

    @Override
    public List<ScheduleActivityBannerDTO> collectRecentActivityBanners(UUID menteeId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<EnrollmentSchedule> allSchedules = enrollmentScheduleRepository.findAllByMenteeId(menteeId);
        List<ScheduleActivityBannerDTO> activityBanners = new ArrayList<>();

        for (EnrollmentSchedule schedule : allSchedules) {
            if (schedule.getRescheduleStatusUpdateDate() != null && schedule.getRescheduleStatusUpdateDate().isAfter(sevenDaysAgo)) {
                String message = generateActivityMessage(schedule);
                if (message != null) {
                    activityBanners.add(new ScheduleActivityBannerDTO(message, schedule.getRescheduleStatusUpdateDate()));
                }
            }
        }

        activityBanners.sort(Comparator.comparing(ScheduleActivityBannerDTO::getUpdateDate).reversed());

        return activityBanners;
    }
    @Override
    public Long countEnrollmentSchedulesHaveRescheduleRequest(Enrollment enrollment){
        return enrollmentScheduleRepository.countByEnrollmentAndRescheduleStatusNot(enrollment, EnrollmentSchedule.RescheduleStatus.NONE);
    }


    private String generateActivityMessage(EnrollmentSchedule schedule) {
        String courseName = schedule.getEnrollment().getCourseMentor().getCourse().getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        switch (schedule.getRescheduleStatus()) {
            case APPROVED:
                return String.format("✅ [Approved] The reschedule request for '%s' to %s has been approved by your mentor.",
                        courseName, schedule.getDate().format(formatter));
            case REJECTED:
                if ("AUTO_REJECTED_EXPIRED_ORIGINAL_DATE".equals(schedule.getRescheduleReason())) {
                    return String.format("️⚠️ [Canceled] The session for '%s' on %s was canceled because the reschedule request expired.",
                            courseName, schedule.getDate().format(formatter));
                } else {
                    String reason = schedule.getRescheduleReason().replace("Mentor: ", "");
                    return String.format("❌ [Rejected] The reschedule request for '%s' has been rejected. Reason: \"%s\"", courseName, reason);
                }
            case NONE:
                if ("AUTO_CANCELED_EXPIRED_REQUESTED_DATE".equals(schedule.getRescheduleReason())) {
                    return String.format("ℹ️ [Expired] The reschedule request for '%s' has expired. The original schedule has been restored.", courseName);
                }
        }
        return null;
    }


    private void sendRescheduleNotificationToMentor(EnrollmentSchedule schedule) {
        Mentor mentor = schedule.getEnrollment().getCourseMentor().getMentor();
        Mentee mentee = schedule.getEnrollment().getMentee();
        String courseName = schedule.getEnrollment().getCourseMentor().getCourse().getName();
        String mentorEmail = mentor.getEmail();
        String subject = "New Reschedule Request from " + mentee.getFullName();

        String reviewUrl = "http://localhost:6969/mentor/requests";
        String text = String.format(
                "Hello %s,\n\nYou have a new reschedule request from mentee %s for the course '%s'.\n\nPlease review the request by visiting the link below:\n%s",
                mentor.getFullName(),
                mentee.getFullName(),
                courseName,
                reviewUrl
        );

        emailService.sendSimpleMail(mentorEmail, subject, text);
    }

    @Override
    public List<EnrollmentSchedule> getPendingRequestsForMentor(UUID mentorId) {
        return enrollmentScheduleRepository.findPendingRequestsForMentor(mentorId);
    }

    @Override
    @Transactional
    public void approveRescheduleRequest(int scheduleId) {
        EnrollmentSchedule schedule = findById(scheduleId);
        if (schedule == null || schedule.getRescheduleStatus() != EnrollmentSchedule.RescheduleStatus.REQUESTED) {
            return;
        }
        LocalDate newDate = schedule.getRequestedNewDate();
        Slot newSlot = schedule.getRequestedNewSlot();
        Mentor mentor = schedule.getEnrollment().getCourseMentor().getMentor();
        Mentee mentee = schedule.getEnrollment().getMentee();

        schedule.setDate(newDate);
        schedule.setSlot(newSlot);
        schedule.setRescheduleStatus(EnrollmentSchedule.RescheduleStatus.APPROVED);
        schedule.setRescheduleStatusUpdateDate(LocalDateTime.now());
        schedule.setRequestedNewDate(null);
        schedule.setRequestedNewSlot(null);
        schedule.setRescheduleReason(null);
        save(schedule);

        MentorAvailableTimeDetails newMentorAvailability = new MentorAvailableTimeDetails(mentor, newSlot, newDate);
        newMentorAvailability.setMentee(mentee);
        mentorAvailableTimeDetailsRepository.save(newMentorAvailability);
    }

    @Override
    @Transactional
    public void rejectRescheduleRequest(int scheduleId, String reason) {
        EnrollmentSchedule schedule = findById(scheduleId);
        if (schedule == null || schedule.getRescheduleStatus() != EnrollmentSchedule.RescheduleStatus.REQUESTED) {
            return;
        }
        schedule.setRescheduleStatus(EnrollmentSchedule.RescheduleStatus.REJECTED);
        schedule.setRescheduleStatusUpdateDate(LocalDateTime.now());
        schedule.setRescheduleReason("Mentor: " + reason);
        schedule.setRequestedNewDate(null);
        schedule.setRequestedNewSlot(null);
        save(schedule);
    }

    @Override
    public Page<EnrollmentAttendanceDTO> findAllSchedulesToBeConfirmed(Pageable pageable) {
        Page<EnrollmentAttendanceProjection> projections = enrollmentScheduleRepository.findAllSchedulesToBeConfirmed(pageable);

        List<EnrollmentAttendanceDTO> dtoList = projections.stream().map(projection -> {
            Optional<Mentee> mentee = menteeRepository.findById(projection.getMenteeId());
            if (mentee.isEmpty()) {
                return null;
            }

            CourseMentor courseMentor = null;
            try {
                courseMentor = courseMentorRepository.findById(projection.getCourseMentorId());
            } catch (RuntimeException e) {
                return null;
            }

            return new EnrollmentAttendanceDTO(
                    projection.getId(),
                    mentee.get(),
                    courseMentor
            );
        }).filter(Objects::nonNull).toList();

        return new PageImpl<>(dtoList, pageable, projections.getTotalElements());
    }

    @Override
    public Page<EnrollmentSchedule> findScheduleByEnrollment(Long enrollmentId, Pageable pageable) {
        return enrollmentScheduleRepository.findSchedulesByEnrollment(enrollmentId, pageable);
    }

}
