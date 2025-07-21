package com.example.edutrack.profiles.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.dto.CourseApplicationDetail;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class CvController {
    private static final Logger logger = LoggerFactory.getLogger(CvController.class);
    public static final int PAGE_SIZE = 15;

    private final CvService cvService;
    private final CourseService courseService;
    private final CourseMentorRepository courseMentorRepository;
    private final CvRepository cvRepository;
    private final MentorRepository mentorRepository;
    private final CourseMentorService courseMentorService;


    public CvController(CvService cvService,
                        CourseService courseService,
                        CourseMentorRepository courseMentorRepository,
                        CvRepository cvRepository,
                        MentorRepository mentorRepository,
                        CourseMentorService courseMentorService) {
        this.cvService = cvService;
        this.courseService = courseService;
        this.courseMentorRepository = courseMentorRepository;
        this.cvRepository = cvRepository;
        this.mentorRepository = mentorRepository;
        this.courseMentorService = courseMentorService;
        logger.info("CvController initialized");
    }

    @GetMapping("/manager/cv/list/{page}")
    public String listCVs(@ModelAttribute CVFilterForm params, Model model, @PathVariable int page) {
        logger.info("Entering listCVs: page={}, filter={}, sort={}, tags={}",
                page, params.getFilter(), params.getSort(), params.getTags());

        if (page - 1 < 0) {
            logger.warn("Invalid page number: {}", page);
            return "redirect:/404";
        }



        String filter = params.getFilter();
        String sort = params.getSort();
        List<String> tags = params.getTags();
        List<String> uniqueSkills = Optional.ofNullable(cvService.getAllUniqueSkills())
                .orElse(new ArrayList<>());
        boolean running = cvService.isBatchRunning();
        LocalDateTime lastEnd = cvService.getLastBatchEnd();
        long delaySeconds = 60;
        long nextBatchMillis = -1;
        if (!running && lastEnd != null) {
            nextBatchMillis = lastEnd.plusSeconds(delaySeconds)
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
        }

        logger.debug("Batch status: running={}, lastEnd={}, nextBatchMillis={}", running, lastEnd, nextBatchMillis);

        if (tags != null && !tags.isEmpty() && !tags.contains("all")) {
            for (String tag : tags) {
                if (tag == null || tag.trim().isEmpty() || !uniqueSkills.contains(tag)) {
                    logger.warn("Invalid tag: {}", tag);
                    return "redirect:/error";
                }
            }
        }


        if (tags == null || tags.isEmpty() || tags.contains("all")) {
            tags = uniqueSkills;
            logger.debug("Tags set to all unique skills: {}", tags);
        }

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Page<CV> cvPage = cvService.queryCVs(filter, sort, tags, pageable);
        if (cvPage == null) {
            logger.error("cvService.queryCVs returned null");
            return "redirect:/error";
        }
        logger.info("Retrieved CV page: pageNumber={}, totalPages={}, totalElements={}",
                page, cvPage.getTotalPages(), cvPage.getTotalElements());

        model.addAttribute("pageNumber", page);
        model.addAttribute("page", cvPage);
        model.addAttribute("skills", uniqueSkills);
        model.addAttribute("nextBatchMillis", nextBatchMillis);
        model.addAttribute("batchRunning", running);

        if (sort != null) {
            model.addAttribute("sort", sort);
            logger.debug("Applied sort: {}", sort);
        }
        if (filter != null) {
            model.addAttribute("filter", filter);
            logger.debug("Applied filter: {}", filter);
        }
        model.addAttribute("tags", tags);

        if (cvPage.getTotalPages() == 0 && page > 1 || page > cvPage.getTotalPages()) {
            logger.warn("Page number {} exceeds total pages {}", page, cvPage.getTotalPages());
            return "redirect:/404";
        }

        logger.info("Exiting listCVs: returning view /cv/list-cv");
        return "/cv/list-cv";
    }

    @GetMapping("/manager/cv/list")
    public String redirectToListCVs() {
        logger.info("Redirecting to /manager/cv/list/1");
        return "redirect:/manager/cv/list/1";
    }

    @GetMapping("mentor/cv/create")
    public String showCVForm(Model model,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "1") int registeredPage,
                             @RequestParam(defaultValue = "3") int size,
                             HttpSession session) {
        logger.info("Entering showCVForm: page={}, registeredPage={}, size={}", page, registeredPage, size);

        if (page - 1 < 0) {
            logger.warn("Invalid page number: {}", page);
            return "redirect:/404";
        }
        Object sessionUser = session.getAttribute("loggedInUser");
        if (!(sessionUser instanceof User user)) {
            logger.warn("Session user is not valid");
            return "redirect:/login";
        }

        UUID userId = user.getId();
        if (userId == null) {
            logger.error("User ID is null");
            return "redirect:/error";
        }
        logger.debug("Processing for userId={}", userId);

        Optional<CV> existingCv = cvRepository.findByUserId(userId);
        logger.debug("Existing CV found: {}", existingCv.isPresent());

        List<CourseMentor> registered = courseMentorService.findByMentorId(userId);
        List<UUID> registeredCourseIds = registered.stream()
                .map(CourseMentor::getCourse)
                .filter(Objects::nonNull)
                .map(Course::getId)
                .filter(Objects::nonNull)
                .toList();
        logger.debug("Registered courses: count={}", registered.size());

        Pageable pageable = PageRequest.of(page - 1, size);
        Pageable registeredPageable = PageRequest.of(registeredPage - 1, size);

        Page<Course> availableCoursePage = courseService.findAllExcludingIds(registeredCourseIds, pageable);
        Page<CourseMentor> registeredCoursePage = courseMentorService.findByMentorIdPaged(userId, registeredPageable);
        if (availableCoursePage == null || registeredCoursePage == null) {
            logger.error("Course pages could not be retrieved");
            return "redirect:/error";
        }

        logger.info("Available courses: page={}, totalPages={}, totalElements={}",
                page, availableCoursePage.getTotalPages(), availableCoursePage.getTotalElements());
        logger.info("Registered courses: page={}, totalPages={}, totalElements={}",
                registeredPage, registeredCoursePage.getTotalPages(), registeredCoursePage.getTotalElements());

        if (page > availableCoursePage.getTotalPages() && availableCoursePage.getTotalPages() > 0) {
            logger.warn("Page {} exceeds availableCoursePage total pages", page);
            return "redirect:/404";
        }

        model.addAttribute("cv", new CVForm());
        model.addAttribute("availableCoursePage", availableCoursePage);
        model.addAttribute("registeredCoursePage", registeredCoursePage);
        model.addAttribute("pageNumber", page);
        model.addAttribute("registeredPageNumber", registeredPage);
        model.addAttribute("userId", userId);

        if (existingCv.isPresent()) {
            CV cv = existingCv.get();
            model.addAttribute("cv", cv);
            model.addAttribute("cvStatus", cv.getStatus());
            logger.info("Existing CV loaded: status={}", cv.getStatus());
            return "cv/create-cv";
        } else {
            model.addAttribute("cv", new CVForm());
            model.addAttribute("cvStatus", "new");
            logger.info("New CV form initialized");
            return "cv/create-cv";
        }
    }

    @PostMapping("mentor/cv/create")
    public String handleCVFormSubmission(@ModelAttribute("cv") CVForm request, Model model, HttpSession session) {
        logger.info("Entering handleCVFormSubmission");

        Object sessionUser = session.getAttribute("loggedInUser");
        if (!(sessionUser instanceof User user)) {
            logger.warn("Invalid or missing session user during CV submission");
            return "redirect:/login";
        }

        UUID userId = user.getId();
        if (userId == null) {
            logger.error("User ID is null in CV submission");
            return "redirect:/error";
        }
        logger.debug("Submitting CV for userId={}", userId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            try {
                Map<String, CourseApplicationDetail> parsedMap =
                        objectMapper.readValue(request.getSelectedCourses(), new TypeReference<>() {});
                request.setCourseDetails(new ArrayList<>(parsedMap.values()));
            } catch (IOException e) {
                logger.error("Failed to parse selectedCourses JSON", e);
                model.addAttribute("error", "Invalid course details submitted.");
                return "redirect:/error";
            }
            cvService.createCV(request, userId);
            logger.info("CV created successfully for userId={}", userId);
            model.addAttribute("message", "CV created successfully!");
            return "redirect:/mentor";
        } catch (Exception e) {
            logger.error("Error creating CV for userId={}", userId, e);
            model.addAttribute("error", "Failed to create CV: " + e.getMessage());
            return "redirect:/404";
        }
    }

    @GetMapping("mentor/cv/edit/{id}")
    public String editCV(@PathVariable("id") UUID id, Model model) {
        logger.info("Entering editCV: id={}", id);

        CV cv = null;
        try {
            cv = cvService.getCVById(id);
            logger.debug("CV retrieved: id={}", id);
        } catch (IllegalArgumentException e) {
            logger.error("CV not found: id={}", id, e);
            model.addAttribute("error", "CV not found.");
            return "redirect:/404";
        }

        model.addAttribute("cv", cv);
        logger.info("Returning view cv/edit-cv for CV id={}", id);
        return "cv/edit-cv";
    }

    @PostMapping("mentor/cv/edit/{id}")
    public String handleEditCV(@PathVariable("id") UUID id, @ModelAttribute("cv") CVForm cvForm, RedirectAttributes redirectAttributes, HttpSession session) {
        logger.info("Entering handleEditCV: id={}", id);

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            logger.warn("No user found in session for CV edit: id={}", id);
            return "redirect:/login";
        }

        try {
            cvForm.setUserId(id);
            cvService.createCV(cvForm, user.getId());
            logger.info("CV updated successfully: id={}", id);
            redirectAttributes.addFlashAttribute("success", "CV updated successfully.");
            return "redirect:/mentor/cv/edit/" + id;
        } catch (IllegalArgumentException e) {
            logger.error("Error updating CV: id={}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error updating CV: " + e.getMessage());
            return "redirect:/mentor/cv/edit/" + id;
        }
    }

    @GetMapping("/manager/cv/detail/{id}")
    public String detailCV(@PathVariable("id") UUID id, Model model) {
        logger.info("Entering detailCV: id={}", id);

        CV cv = cvService.getCVById(id);
        if (cv == null) {
            logger.warn("CV not found: id={}", id);
            model.addAttribute("cv", null);
            model.addAttribute("registeredCourses", null);
            return "cv/cv-detail";
        }

        Optional<Mentor> mentorOpt = mentorRepository.findById(cv.getId());
        if (mentorOpt.isPresent()) {
            List<CourseMentor> registeredCourses = courseMentorRepository.findAllByMentor(mentorOpt.get());
            logger.debug("Registered courses found for CV id={}: count={}", id, registeredCourses.size());
            model.addAttribute("cv", cv);
            model.addAttribute("registeredCourses", registeredCourses);
        } else {
            logger.warn("Mentor not found for CV id={}", id);
            model.addAttribute("cv", cv);
            model.addAttribute("registeredCourses", null);
        }

        logger.info("Returning view cv/cv-detail for CV id={}", id);
        return "cv/cv-detail";
    }

    @PostMapping("/cv/accept/{id}")
    public String acceptCv(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        logger.info("Entering acceptCv: id={}", id);

        if (cvService.acceptCV(id)) {
            logger.info("CV accepted successfully: id={}", id);
            redirectAttributes.addFlashAttribute("success", "CV accepted successfully.");
        } else {
            logger.warn("Failed to accept CV: id={}, must be in 'Pending' status", id);
            redirectAttributes.addFlashAttribute("error", "CV must be in 'Pending' status.");
        }

        logger.info("Redirecting to /manager/cv/list/1");
        return "redirect:/manager/cv/list/1";
    }

    @PostMapping("/cv/reject/{id}")
    public String rejectCv(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        logger.info("Entering rejectCv: id={}", id);

        if (cvService.rejectCV(id)) {
            logger.info("CV rejected successfully: id={}", id);
            redirectAttributes.addFlashAttribute("success", "CV rejected successfully.");
        } else {
            logger.warn("Failed to reject CV: id={}, must be in 'Pending' status", id);
            redirectAttributes.addFlashAttribute("error", "CV must be in 'Pending' status.");
        }

        logger.info("Redirecting to /manager/cv/list/1");
        return "redirect:/manager/cv/list/1";
    }

    @GetMapping("/mentor/cv/course-sections")
    public String getCourseSections(Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "1") int registeredPage,
                                    @RequestParam(defaultValue = "3") int size,
                                    @RequestParam UUID userId,
                                    HttpSession session) {
        logger.info("Entering getCourseSections: page={}, registeredPage={}, size={}, userId={}",
                page, registeredPage, size, userId);

        if (page < 1 || registeredPage < 1) {
            logger.error("Invalid page numbers: page={}, registeredPage={}", page, registeredPage);
            throw new IllegalArgumentException("Page numbers must be positive");
        }

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getId().equals(userId)) {
            logger.warn("Session validation failed: userId={}, sessionUser={}",
                    userId, user != null ? user.getId() : null);
            throw new IllegalArgumentException("User not authenticated or invalid userId");
        }

        List<CourseMentor> registered = courseMentorService.findByMentorId(userId);
        List<UUID> registeredCourseIds = registered.stream().map(cm -> cm.getCourse().getId()).toList();
        logger.debug("Registered courses for userId={}: count={}", userId, registered.size());

        Pageable pageable = PageRequest.of(page - 1, size);
        Pageable registeredPageable = PageRequest.of(registeredPage - 1, size);

        Page<Course> availableCoursePage = courseService.findAllExcludingIds(registeredCourseIds, pageable);
        Page<CourseMentor> registeredCoursePage = courseMentorService.findByMentorIdPaged(userId, registeredPageable);
        logger.info("Available courses: page={}, totalPages={}, totalElements={}",
                page, availableCoursePage.getTotalPages(), availableCoursePage.getTotalElements());
        logger.info("Registered courses: page={}, totalPages={}, totalElements={}",
                registeredPage, registeredCoursePage.getTotalPages(), registeredCoursePage.getTotalElements());

        Optional<CV> existingCv = cvRepository.findByUserId(userId);
        String cvStatus = existingCv.isPresent() ? existingCv.get().getStatus() : "new";
        logger.debug("CV status for userId={}: {}", userId, cvStatus);

        model.addAttribute("availableCoursePage", availableCoursePage);
        model.addAttribute("registeredCoursePage", registeredCoursePage);
        model.addAttribute("pageNumber", page);
        model.addAttribute("registeredPageNumber", registeredPage);
        model.addAttribute("cvStatus", cvStatus);
        model.addAttribute("userId", userId);

        logger.info("Returning fragment course-sections for userId={}", userId);
        return "fragments/course-sections :: courseSections";
    }

    @PostMapping("/mentor/cv/course/add")
    public String addCourse(@RequestParam UUID courseId,
                            @RequestParam UUID userId,
                            HttpSession session) {
        logger.info("Entering addCourse: courseId={}, userId={}", courseId, userId);

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getId().equals(userId)) {
            logger.warn("Session validation failed for addCourse: userId={}, sessionUser={}",
                    userId, user != null ? user.getId() : null);
            throw new IllegalArgumentException("User not authenticated or invalid userId");
        }

        Optional<CV> existingCv = cvRepository.findByUserId(userId);
        String cvStatus = existingCv.isPresent() ? existingCv.get().getStatus() : "new";
        logger.debug("CV status for userId={}: {}", userId, cvStatus);
        if (!cvStatus.equals("new") && !cvStatus.equals("rejected")) {
            logger.warn("Cannot add course: CV status is {}", cvStatus);
            throw new IllegalArgumentException("Cannot add course: CV is not editable");
        }

        try {
            courseMentorService.addCourseMentor(userId, courseId, "");
            logger.info("Course added successfully: courseId={}, userId={}", courseId, userId);
            return "redirect:/mentor/cv/create";
        } catch (Exception e) {
            logger.error("Error adding course: courseId={}, userId={}", courseId, userId, e);
            throw new IllegalArgumentException("Failed to add course: " + e.getMessage());
        }
    }

    @PostMapping("/mentor/cv/course/remove")
    public String removeCourse(@RequestParam UUID courseId,
                               @RequestParam UUID userId,
                               HttpSession session) {
        logger.info("Entering removeCourse: courseId={}, userId={}", courseId, userId);

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getId().equals(userId)) {
            logger.warn("Session validation failed for removeCourse: userId={}, sessionUser={}",
                    userId, user != null ? user.getId() : null);
            throw new IllegalArgumentException("User not authenticated or invalid userId");
        }

        Optional<CV> existingCv = cvRepository.findByUserId(userId);
        String cvStatus = existingCv.isPresent() ? existingCv.get().getStatus() : "new";
        logger.debug("CV status for userId={}: {}", userId, cvStatus);
        if (!cvStatus.equals("new") && !cvStatus.equals("rejected")) {
            logger.warn("Cannot remove course: CV status is {}", cvStatus);
            throw new IllegalArgumentException("Cannot remove course: CV is not editable");
        }

        try {
            courseMentorService.removeCourseMentor(userId, courseId);
            logger.info("Course removed successfully: courseId={}, userId={}", courseId, userId);
            return "redirect:/mentor/cv/create";
        } catch (Exception e) {
            logger.error("Error removing course: courseId={}, userId={}", courseId, userId, e);
            throw new IllegalArgumentException("Failed to remove course: " + e.getMessage());
        }
    }

    @PostMapping("/mentor/cv/course/update-description")
    public String updateCourseDescription(@RequestParam UUID courseId,
                                          @RequestParam UUID userId,
                                          @RequestParam String description,
                                          HttpSession session) {
        logger.info("Entering updateCourseDescription: courseId={}, userId={}, descriptionLength={}",
                courseId, userId, description.length());

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !user.getId().equals(userId)) {
            logger.warn("Session validation failed for updateCourseDescription: userId={}, sessionUser={}",
                    userId, user != null ? user.getId() : null);
            throw new IllegalArgumentException("User not authenticated or invalid userId");
        }

        Optional<CV> existingCv = cvRepository.findByUserId(userId);
        String cvStatus = existingCv.isPresent() ? existingCv.get().getStatus() : "new";
        logger.debug("CV status for userId={}: {}", userId, cvStatus);
        if (!cvStatus.equals("new") && !cvStatus.equals("rejected")) {
            logger.warn("Cannot update description: CV status is {}", cvStatus);
            throw new IllegalArgumentException("Cannot update description: CV is not editable");
        }

        try {
            courseMentorService.updateCourseMentorDescription(userId, courseId, description);
            logger.info("Description updated successfully: courseId={}, userId={}", courseId, userId);
            return "redirect:/mentor/cv/create";
        } catch (Exception e) {
            logger.error("Error updating description: courseId={}, userId={}", courseId, userId, e);
            throw new IllegalArgumentException("Failed to update description: " + e.getMessage());
        }
    }
}