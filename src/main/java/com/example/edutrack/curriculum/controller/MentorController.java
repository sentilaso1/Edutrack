package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.common.service.implementations.EmailService;
import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import jakarta.servlet.http.HttpSession;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller(value = "mentee")
public class MentorController {
    private final MentorService mentorService;
    private final CourseMentorServiceImpl courseMentorServiceImpl;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final CourseMentorService courseMentorService;
    private final CourseMentorRepository courseMentorRepository;
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final EmailService emailService;

    public MentorController(MentorService mentorService,
                            CourseMentorServiceImpl courseMentorServiceImpl,
                            MentorAvailableTimeService mentorAvailableTimeService,
                            FeedbackRepository feedbackRepository,
                            CourseRepository courseRepository,
                            CourseMentorService courseMentorService,
                            CourseMentorRepository courseMentorRepository,
                            EnrollmentScheduleService enrollmentScheduleService,
                            EmailService emailService) {
        this.mentorService = mentorService;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.feedbackRepository = feedbackRepository;
        this.courseRepository = courseRepository;
        this.courseMentorService = courseMentorService;
        this.courseMentorRepository = courseMentorRepository;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.emailService = emailService;
    }

    private UUID getSessionMentor(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return null;
        }
        return loggedInUser.getId();
    }

    @GetMapping("/mentors")
    public String viewMentorList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "") String[] expertise,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Integer totalSessions,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(value="skill", required=false) List<String> selectedSkills,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size_page,
            @RequestParam(required = false) String order_by,
            Model model) {

        if (page < 1) {
            return "redirect:/404";
        }

        if (size_page < 1) {
            size_page = 6;
        }

        Sort sort;
        if ("newest".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.DESC, "createdDate");
        } else if ("oldest".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.ASC, "createdDate");
        } else if ("name_asc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.ASC, "fullName");
        } else if ("name_desc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.DESC, "fullName");
        } else if ("rating_desc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("rating_asc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.ASC, "rating");
        } else {
            sort = Sort.unsorted();
        }

        Pageable pageable = PageRequest.of(page - 1, size_page, sort);

        Page<Mentor> mentorPage = mentorService.searchMentorsWithApprovedCV(
                name, expertise, rating, totalSessions, isAvailable, pageable
        );

        if (mentorPage == null) {
            return "redirect:/error";
        }

        List<String> allSkills = mentorService.getAllMentorExpertiseFromApprovedCVs();

        model.addAttribute("allExpertiseSkills", allSkills);
        model.addAttribute("mentorPage", mentorPage);
        model.addAttribute("page", page);
        model.addAttribute("name", name);
        model.addAttribute("expertise", expertise);

        model.addAttribute("rating", rating);
        model.addAttribute("selectedSkills", selectedSkills);
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("expertiseInput", String.join(", ", expertise));
        model.addAttribute("isAvailable", isAvailable);
        model.addAttribute("order_by", order_by);

        return "mentee/mentor-list";
    }

    @GetMapping("/mentors/{id}")
    public String viewMentorDetail(@PathVariable UUID id,
                                   @RequestParam(value = "month", required = false) String month,
                                   Model model,
                                   HttpSession session){
        List<CourseMentor> courseMentors = courseMentorServiceImpl.getCourseMentorByMentorId(id);

        LocalDate now = LocalDate.now();
        LocalDate selectedMonth = (month != null)
                ? LocalDate.parse(month + "-01")
                : now.withDayOfMonth(1);

        LocalDate endLocal = selectedMonth.withDayOfMonth(selectedMonth.lengthOfMonth());

        Mentor mentor = mentorService.getMentorById(id).get();
        List<MentorAvailableSlotDTO> setSlots = mentorAvailableTimeService.findOnlyApprovedSlotsByEndDate(mentor, endLocal);
        boolean[][] slotDayMatrix = new boolean[Slot.values().length][Day.values().length];

        List<Map<String, String>> selectableMonths = new ArrayList<>();
        DateTimeFormatter valueFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        for (int i = 0; i < 5; i++) {
            LocalDate monthDate = now.plusMonths(i);
            String value = monthDate.format(valueFormatter);
            String label = monthDate.format(labelFormatter);
            Map<String, String> item = new HashMap<>();
            item.put("value", value);
            item.put("label", label);
            selectableMonths.add(item);
        }

        model.addAttribute("slotDayMatrix", slotDayMatrix);
        model.addAttribute("slots", Slot.values());
        model.addAttribute("dayLabels", Day.values());

        List<Feedback> feedbacks = feedbackRepository.findByCourseMentor_Mentor_IdAndStatus(id, Feedback.Status.ACTIVE);

        model.addAttribute("selectableMonths", selectableMonths);
        model.addAttribute("selectedMonth", selectedMonth.format(valueFormatter));

        model.addAttribute("isLoggedIn", session.getAttribute("loggedInUser") != null);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("mentor", mentorService.getMentorById(id));
        model.addAttribute("courses", courseMentors);
        model.addAttribute("subjects", mentorService.getTagsByMentor(id));
        model.addAttribute("cv", mentorService.getCVById(id));
        return "mentor-course";
    }

    @GetMapping("/mentors/{mentorId}/review")
    public String showReviewForm(@PathVariable UUID mentorId,
                                 @RequestParam UUID courseId,
                                 HttpSession session,
                                 Model model) {
        Optional<Mentor> mentorOpt = mentorService.getMentorById(mentorId);

        if (mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();
            model.addAttribute("mentor", mentor);
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            model.addAttribute("course", course);
        }

        Mentee mentee = (Mentee) session.getAttribute("loggedInUser");

        UUID menteeId = mentee.getId();
        model.addAttribute("menteeId", menteeId);
        model.addAttribute("courseId", courseId);

        return "mentee/mentor-review";
    }


    @GetMapping("/mentor/price")
    public String listMentorCourses(Model model,
                                    HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        List<CourseMentor> courseMentors = courseMentorRepository.findByMentorIdAndStatus(user.getId(), ApplicationStatus.ACCEPTED);
        model.addAttribute("courseMentors", courseMentors);
        return "mentor/skill-price-set";
    }

    @PostMapping("/mentor/price/save")
    public String saveAllPrices(
            @RequestParam("courseId") List<UUID> courseIds,
            @RequestParam("price") List<Double> prices,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        courseMentorService.updatePrices(mentor.getId(), courseIds, prices);
        redirectAttributes.addFlashAttribute("success", "Prices updated!");
        return "redirect:/mentor/price";
    }

    @GetMapping("/mentors/{id}/schedule-table")
    public String getScheduleTable(
            @PathVariable UUID id,
            @RequestParam("month") String month,
            Model model
    ) {
        LocalDate selectedMonth = LocalDate.parse(month + "-01");
        LocalDate endLocal = selectedMonth.withDayOfMonth(selectedMonth.lengthOfMonth());

        Mentor mentor = mentorService.getMentorById(id).get();

        List<MentorAvailableSlotDTO> setSlots = mentorAvailableTimeService.findOnlyApprovedSlotsByEndDate(mentor, endLocal);
        boolean[][] slotDayMatrix = new boolean[Slot.values().length][Day.values().length];

        for (MentorAvailableSlotDTO dto : setSlots) {
            int slotIndex = dto.getSlot().ordinal();
            int dayIndex = dto.getDay().ordinal();
            slotDayMatrix[slotIndex][dayIndex] = true;
        }

        model.addAttribute("slotDayMatrix", slotDayMatrix);
        model.addAttribute("slots", Slot.values());
        model.addAttribute("dayLabels", Day.values());

        return "fragments/schedule-table :: scheduleTable";
    }


    @GetMapping("/mentor/requests")
    public String showRescheduleRequests(Model model, HttpSession session) {
        UUID mentorId = getSessionMentor(session);
        if (mentorId == null) return "redirect:/login";
        List<EnrollmentSchedule> requests = enrollmentScheduleService.getPendingRequestsForMentor(mentorId);
        model.addAttribute("requests", requests);
        return "mentor/mentor-requests";
    }

    @PostMapping("/mentor/requests/approve")
    public String handleApproveRequest(@RequestParam("scheduleId") int scheduleId, RedirectAttributes redirectAttributes, HttpSession session) {
        UUID mentorId = getSessionMentor(session);
        if (mentorId == null) return "redirect:/login";

        enrollmentScheduleService.approveRescheduleRequest(scheduleId);
        redirectAttributes.addFlashAttribute("successMessage", "Request approved successfully!");
        return "redirect:/mentor/requests";
    }

    @PostMapping("/mentor/reschedule-request")
    public String mentorRescheduleRequest(@RequestParam String reason, @RequestParam int eid){
        if(reason == null || reason.trim().isEmpty()){
            return "redirect:/mentor/schedule/" + eid +"?error=Please enter reason";
        }
        EnrollmentSchedule enrollmentSchedule = enrollmentScheduleService.findById(eid);
        enrollmentSchedule.setRescheduleReason("MENTOR: " + reason);
        enrollmentScheduleService.save(enrollmentSchedule);
        //send email to notify mentee
        String menteeEmail = enrollmentSchedule.getEnrollment().getMentee().getEmail();
        String mentorName = enrollmentSchedule.getEnrollment().getCourseMentor().getMentor().getFullName();
        String menteeName = enrollmentSchedule.getEnrollment().getMentee().getFullName();
        String skillName = enrollmentSchedule.getEnrollment().getCourseMentor().getCourse().getName();
        String scheduleDate = enrollmentSchedule.getDate().toString();
        String link = "http://localhost:6969/schedules/reschedule?scheduleId=" + eid;

        String subject = "RESCHEDULE REQUESTS: From Mentor " + mentorName;

        String body = "Hello " + menteeName + ",\n\n"
                      + "Mentor " + mentorName
                      + " who teaches skill \"" + skillName + "\" "
                      + "would like to change the schedule on " + scheduleDate + " to another date.\n"
                      + "Reason: " + reason + "\n\n"
                      + "Please click the link below to choose a new schedule:\n"
                      + link + "\n\n"
                      + "Best regards,\n"
                      + "EduTrack";
        emailService.sendSimpleMail(menteeEmail, subject, body);

        return "redirect:/mentor/schedule/" + eid + "?success=true";
    }

    @PostMapping("/mentor/requests/reject")
    public String handleRejectRequest(
            @RequestParam("scheduleId") int scheduleId,
            @RequestParam("reason") String reason,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        UUID mentorId = getSessionMentor(session);
        if (mentorId == null) return "redirect:/login";

        if (reason == null || reason.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rejection reason cannot be empty.");
            return "redirect:/mentor/requests";
        }

        enrollmentScheduleService.rejectRescheduleRequest(scheduleId, reason.trim());
        redirectAttributes.addFlashAttribute("successMessage", "Request rejected successfully.");
        return "redirect:/mentor/requests";
    }

    @GetMapping("/mentor/your-review")
    public String showMentorReview(Model model, HttpSession session) {
        if (session == null) {
            return "redirect:/login";
        }
        Mentor currentMentor = (Mentor) session.getAttribute("loggedInUser");
        UUID id = currentMentor.getId();
        List<Feedback> feedbacks = feedbackRepository.findByCourseMentor_Mentor_IdAndStatus(id, Feedback.Status.ACTIVE);
        model.addAttribute("feedbacks", feedbacks);
        return "mentor/mentor-review";
    }
}
