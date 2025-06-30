package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.service.MentorService;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller(value = "mentee")
public class MentorController {
    private final MentorService mentorService;
    private final CourseMentorServiceImpl courseMentorServiceImpl;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;

    public MentorController(MentorService mentorService,
                            CourseMentorServiceImpl courseMentorServiceImpl,
                            MentorAvailableTimeService mentorAvailableTimeService,
                            FeedbackRepository feedbackRepository,
                            CourseRepository courseRepository) {
        this.mentorService = mentorService;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.feedbackRepository = feedbackRepository;
        this.courseRepository = courseRepository;
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

        Page<Mentor> mentorPage = mentorService.searchMentors(
                name, expertise, rating, totalSessions, isAvailable, pageable
        );



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
    public String viewMentorDetail(@PathVariable UUID id, Model model){
        List<CourseMentor> courseMentors = courseMentorServiceImpl.getCourseMentorByMentorId(id);
        Mentor mentor = mentorService.getMentorById(id).get();
        LocalDate endLocal = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<MentorAvailableSlotDTO> setSlots = mentorAvailableTimeService.findAllSlotByEndDate(mentor, endLocal);
        boolean[][] slotDayMatrix = new boolean[Slot.values().length][Day.values().length];

        for (MentorAvailableSlotDTO dto : setSlots) {
            int slotIndex = dto.getSlot().ordinal();
            int dayIndex = dto.getDay().ordinal();
            slotDayMatrix[slotIndex][dayIndex] = true;
        }

        model.addAttribute("slotDayMatrix", slotDayMatrix);
        model.addAttribute("slots", Slot.values());
        model.addAttribute("dayLabels", Day.values());

        List<Feedback> feedbacks = feedbackRepository.findByCourseMentor_Mentor_IdAndStatus(id, Feedback.Status.ACTIVE);

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
}
