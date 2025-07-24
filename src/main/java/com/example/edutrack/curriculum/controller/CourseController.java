package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.implementations.MenteeServiceImpl;
import com.example.edutrack.accounts.service.implementations.MentorServiceImpl;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.*;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.*;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import com.example.edutrack.timetables.model.*;
import com.example.edutrack.timetables.repository.MentorAvailableTimeDetailsRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class
CourseController {
    private final CourseServiceImpl courseServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final CourseTagService courseTagService;
    private final MentorServiceImpl mentorServiceImpl;
    private final TagServiceImpl tagServiceImpl;
    private final CourseMentorService courseMentorService;
    private final CourseMentorServiceImpl courseMentorServiceImpl;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentService enrollmentService;
    private final WalletService walletService;
    private final CourseRepository courseRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final MenteeServiceImpl menteeService;
    private final MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository;
    private final FeedbackService feedbackService;
    private final EnrollmentScheduleService enrollmentScheduleService;

    @Autowired
    public CourseController(CourseServiceImpl courseServiceImpl,
                            FeedbackService feedbackService,
                            CourseTagServiceImpl courseTagServiceImpl,
                            MentorServiceImpl mentorServiceImpl,
                            TagServiceImpl tagServiceImpl,
                            CourseTagService courseTagService,
                            CourseMentorService courseMentorService,
                            CourseMentorServiceImpl courseMentorServiceImpl,
                            MentorAvailableTimeService mentorAvailableTimeService,
                            EnrollmentService enrollmentService,
                            WalletService walletService,
                            CourseRepository courseRepository,
                            CourseMentorRepository courseMentorRepository,
                            MenteeServiceImpl menteeService,
                            MentorAvailableTimeDetailsRepository mentorAvailableTimeDetailsRepository,
                            EnrollmentScheduleService enrollmentScheduleService) {
        this.courseServiceImpl = courseServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.mentorServiceImpl = mentorServiceImpl;
        this.tagServiceImpl = tagServiceImpl;
        this.courseMentorService = courseMentorService;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
        this.courseTagService = courseTagService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentService = enrollmentService;
        this.walletService = walletService;
        this.courseRepository = courseRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.menteeService = menteeService;
        this.mentorAvailableTimeDetailsRepository = mentorAvailableTimeDetailsRepository;
        this.feedbackService = feedbackService;
        this.enrollmentScheduleService = enrollmentScheduleService;
    }

    @GetMapping("/courses")
    public String courses(Model model,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "6") int size_page,
                          @RequestParam(required = false) List<Integer> subject,
                          @RequestParam(required = false) List<UUID> skill,
                          @RequestParam(required = false) String order_by) {

        if (page < 1) {
            return "redirect:/404";
        }

        Pageable pageable = PageRequest.of(page - 1, size_page);
        Page<CourseMentor> coursePage;

        List<Integer> subjectIds;
        if (subject != null && !subject.isEmpty()) {
            subjectIds = subject;
        } else {
            subjectIds = null;
        }
        List<UUID> skillIds;
        if (skill != null && !skill.isEmpty()) {
            skillIds = skill;
        } else {
            skillIds = null;
        }

        if ("newest".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByCreatedDateDesc(pageable);
        } else if ("oldest".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByCreatedDateAsc(pageable);
        } else if ("title_asc".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByTitleAsc(pageable);
        } else if ("title_desc".equalsIgnoreCase(order_by)) {
            coursePage = courseMentorServiceImpl.findAlByOrderByTitleDesc(pageable);
        } else {
            coursePage = courseMentorServiceImpl.findFilteredCourseMentors(skillIds, subjectIds, pageable);
        }

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("page", page);
        model.addAttribute("subjectList", courseMentorServiceImpl.findAllTags());
        model.addAttribute("skillList", courseMentorServiceImpl.findAllCourses());
        model.addAttribute("selectedSubjects", subject);
        model.addAttribute("selectedSkills", skill);

        return "mentee/courselist";
    }

    @GetMapping("/courses-only")
    public String coursesOnly(Model model,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "6") int size_page,
                              @RequestParam(required = false) List<Integer> subject,
                              @RequestParam(required = false) List<UUID> skill,
                              @RequestParam(required = false) String order_by) {

        if (page < 1) {
            return "redirect:/404";
        }

        List<Integer> subjectIds;
        if (subject != null && !subject.isEmpty()) {
            subjectIds = subject;
        } else {
            subjectIds = null;
        }
        List<UUID> skillIds;
        if (skill != null && !skill.isEmpty()) {
            skillIds = skill;
        } else {
            skillIds = null;
        }

        Pageable pageable;
        Page<Course> coursePage;

        if (order_by != null && !order_by.isEmpty()) {
            pageable = switch (order_by.toLowerCase()) {
                case "newest" -> PageRequest.of(page - 1, size_page, Sort.by("createdDate").descending());
                case "oldest" -> PageRequest.of(page - 1, size_page, Sort.by("createdDate"));
                case "title_asc" -> PageRequest.of(page - 1, size_page, Sort.by("name"));
                case "title_desc" -> PageRequest.of(page - 1, size_page, Sort.by("name").descending());
                default -> PageRequest.of(page - 1, size_page);
            };
            coursePage = courseServiceImpl.getAll(pageable);
        } else {
            pageable = PageRequest.of(page - 1, size_page);
            coursePage = courseServiceImpl.findFilteredCourses(skillIds, subjectIds, pageable);
        }

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("page", page);
        model.addAttribute("subjectList", courseMentorServiceImpl.findAllTags());
        model.addAttribute("skillList", courseMentorServiceImpl.findAllCourses());
        model.addAttribute("selectedSubjects", subject);
        model.addAttribute("selectedSkills", skill);

        return "mentee/courselist_onlycourse";
    }

    @GetMapping("/courses/{courseMentorId}")
    public String courseDetail(@PathVariable("courseMentorId") UUID courseMentorId, Model model) {

        CourseMentor courseMentor = courseMentorService.findById(courseMentorId);

        if (courseMentor == null) {
            throw new RuntimeException("Course mentor not found");
        }
        Course course = courseMentor.getCourse();
        Mentor mentor = courseMentor.getMentor();

        long courseCount = courseMentorService.countCoursesByMentor(mentor);
        long reviewCount = feedbackService.countReviewsByMentor(mentor);
        long studentCount = enrollmentService.countStudentsByMentor(mentor);
        List<Tag> tagList = tagServiceImpl.findTagsByCourseId(course.getId());

        List<CourseMentor> relatedCourse = courseMentorService.getRelatedCoursesByTags(course.getId(), null, 6);
        List<CourseCardDTO> relatedCourseToDTO = enrollmentService.mapToCourseCardDTOList(relatedCourse);
        List<Feedback> feedbackList = feedbackService.getTopRecentFeedback(courseMentor);
        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("relatedCourses", relatedCourseToDTO);
        model.addAttribute("course", courseMentor);
        model.addAttribute("tagList", tagList);
        model.addAttribute("mentorCourseCount", courseCount);
        model.addAttribute("mentorReviewCount", reviewCount);
        model.addAttribute("mentorStudentCount", studentCount);

        model.addAttribute("courseMentor", courseMentor);
        return "/mentee/course_detail";
    }


    @GetMapping("/mentors/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id) {
        Optional<Mentor> mentor = mentorServiceImpl.findById(id);
        if (mentor.isEmpty() || mentor.get().getAvatar() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");
        return new ResponseEntity<>(mentor.get().getAvatar(), headers, HttpStatus.OK);
    }

    @GetMapping("courses/register/{cmid}")
    public String registerCourse(@PathVariable UUID cmid, HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Mentee> menteeOpt = menteeService.findById(user.getId());
        if (menteeOpt.isEmpty()) {
            return "redirect:/login";
        }

        CourseMentor courseMentor = courseMentorService.findById(cmid);
        List<MentorAvailableTime> mentorAvailableTime = mentorAvailableTimeService.findByMentorId(courseMentor.getMentor());
        model.addAttribute("courseMentor", courseMentor);
        model.addAttribute("mentorAvailableTime", mentorAvailableTime);

        List<Tag> tags = tagServiceImpl.findTagsByCourseId(courseMentor.getCourse().getId());
        model.addAttribute("tagList", tags);
        LocalDate minDate = mentorAvailableTimeService.findMinStartDate(courseMentor.getMentor());
        if (minDate == null) {
            return "redirect:/courses/" + courseMentor.getId() + "?error=Mentor Schedule haven't been registered yet";
        }
        if (minDate.isBefore(LocalDate.now().plusDays(5))) {
            minDate = LocalDate.now().plusDays(5);
        }
        LocalDate maxDate = mentorAvailableTimeService.findMaxEndDate(courseMentor.getMentor());
        if (maxDate == null) {
            return "redirect:/courses/" + courseMentor.getId() + "?error=Mentor Schedule haven't been registered yet";
        }
        model.addAttribute("minDate", minDate);
        model.addAttribute("maxDate", maxDate);
        model.addAttribute("slots", Slot.values());
        model.addAttribute("dayLabels", Day.values());
        int[][] slotDayMatrix = availableSlotMatrix(courseMentor.getMentor(), menteeOpt.get(), minDate, maxDate);
        if (slotDayMatrix == null) {
            slotDayMatrix = new int[5][10];
        }

        model.addAttribute("slotDayMatrix", slotDayMatrix);

        model.addAttribute("startTime", session.getAttribute("startTime"));

        if (user != null) {
            Optional<Wallet> walletOptional = walletService.findById(user.getId());
            if (walletOptional.isEmpty()) {
                walletOptional = Optional.of(walletService.save(user));
            }
            model.addAttribute("wallet", walletOptional.get());
        }

        return "register-section";
    }

    @GetMapping("/courses/{courseId}/list")
    public String courseMentorList(@PathVariable("courseId") UUID courseId, Model model) {

        Course course = courseRepository.findById(courseId).get();
        List<CourseMentor> courseMentors = courseMentorRepository.findByCourse_IdAndStatus(courseId, ApplicationStatus.ACCEPTED);
        List<Mentor> relatedMentors = courseMentors.stream()
                .map(CourseMentor::getMentor)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("courseMentors", courseMentors);
        model.addAttribute("relatedMentors", relatedMentors);
        model.addAttribute("course", course);

        return "course-related-mentor";
    }

    public int[][] availableSlotMatrix(Mentor mentor,
                                       Mentee mentee,
                                       LocalDate minDate,
                                       LocalDate maxDate) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        if (minDate.isAfter(currentDate)) {
            currentDate = minDate;
        }

        while (!currentDate.isAfter(maxDate)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        int dayCount = dateList.size();
        int[][] availableSlotMatrix = new int[Slot.values().length][dayCount];
        for (int i = 0; i < Slot.values().length; i++) {
            for (int j = 0; j < dayCount; j++) {
                Slot slot = Slot.values()[i];
                LocalDate slotDate = dateList.get(j);
                if (enrollmentService.isValidRequest(mentee, mentor, slot, slotDate)) {
                    availableSlotMatrix[i][j] = enrollmentService.getNumberOfPendingSlot(mentor, slotDate, slot);
                } else {
                    availableSlotMatrix[i][j] = -1;
                }
            }
        }
        return availableSlotMatrix;
    }

}