package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.common.controller.EndpointRegistry;
import com.example.edutrack.curriculum.model.LandingPageConfig;
import com.example.edutrack.curriculum.model.MenteeLandingRole;
import com.example.edutrack.curriculum.service.interfaces.LandingPageConfigService;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.*;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.accounts.service.interfaces.ManagerStatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.edutrack.accounts.dto.RevenueChartDTO;
import com.example.edutrack.accounts.dto.TopMentorDTO;

import java.io.IOException;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;

import com.example.edutrack.accounts.dto.ManagerStatsDTO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ManagerController {
    private final MentorService mentorService;
    private final MenteeService menteeService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final ManagerStatsService managerStatsService;
    private final LandingPageConfigService landingPageConfigService;
    private final EndpointRegistry endpointRegistry;

    public static final int ENROLLMENT_PAGE_SIZE = 30;

    @Autowired
    public ManagerController(MentorService mentorService,
                             MenteeService menteeService,
                             MentorAvailableTimeService mentorAvailableTimeService,
                             EnrollmentService enrollmentServiceImpl,
                             EnrollmentScheduleService enrollmentScheduleService,
                             ManagerStatsService managerStatsService,
                             LandingPageConfigService landingPageConfigService, EndpointRegistry endpointRegistry) {
        this.mentorService = mentorService;
        this.menteeService = menteeService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentService = enrollmentServiceImpl;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.managerStatsService = managerStatsService;
        this.landingPageConfigService = landingPageConfigService;
        this.endpointRegistry = endpointRegistry;
    }

    @GetMapping("/manager/dashboard")
    public String showDashboard(Model model, @RequestParam(defaultValue = "week") String period) {
        ManagerStatsDTO stats = managerStatsService.getManagerStats(period);
        model.addAttribute("stats", stats);
        model.addAttribute("currentPeriod", period);

        return "manager/dashboard";
    }

    @GetMapping("/manager/mentees")
    public String showMentees(Model model) {
        model.addAttribute("mentees", menteeService.findAll());
        return "manager/mentees";
    }

    @GetMapping("/manager/mentors")
    public String showMentors(Model model) {
        model.addAttribute("mentors", mentorService.findAll());
        return "manager/mentors";
    }

    @GetMapping("/manager/api/revenue-chart")
    @ResponseBody
    public ResponseEntity<List<RevenueChartDTO>> getRevenueChart(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime startDate = getStartDateByPeriod(period);
        List<RevenueChartDTO> chartData = managerStatsService.getRevenueChartData(period, startDate);
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/manager/api/top-mentors")
    @ResponseBody
    public ResponseEntity<List<TopMentorDTO>> getTopMentors(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime startDate = getStartDateByPeriod(period);
        List<TopMentorDTO> topMentors = managerStatsService.getTopMentors(startDate);
        return ResponseEntity.ok(topMentors);
    }

    @GetMapping("/manager/api/summary-stats")
    @ResponseBody
    public ResponseEntity<ManagerStatsDTO> getSummaryStats(
            @RequestParam(defaultValue = "week") String period) {
        ManagerStatsDTO stats = managerStatsService.getManagerStats(period);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/manager/schedules")
    public String redirectShowSchedules(@RequestParam(required = false) String menteeId,
                                        @RequestParam(required = false) String mentorId) {
        String queryParams = "";

        if (menteeId != null) queryParams += "&menteeId=" + menteeId;
        if (mentorId != null) queryParams += "&mentorId=" + mentorId;

        return "redirect:/manager/schedules/1" + (queryParams.isEmpty() ? "" : "?" + queryParams.substring(1));
    }

    @GetMapping("/manager/schedules/{page}")
    public String showSchedules(Model model,
                                @PathVariable Integer page,
                                @RequestParam(required = false) String menteeId,
                                @RequestParam(required = false) String mentorId) {

        if (page - 1 < 0) return "redirect:/404";

        Pageable pageable = PageRequest.of(page - 1, ENROLLMENT_PAGE_SIZE);

        model.addAttribute("mentors", enrollmentService.findAllUniqueMentors());
        model.addAttribute("mentees", enrollmentService.findAllUniqueMentees());
        model.addAttribute("page", page);
        model.addAttribute("selectedMentee", menteeId);
        model.addAttribute("selectedMentor", mentorId);

        model.addAttribute(
                "schedulePage",
                enrollmentScheduleService.findAllSchedulesToBeConfirmedFiltered(pageable, menteeId, mentorId)
        );

        return "manager/schedules";
    }

    @GetMapping("/manager/schedules/view/{eid}")
    public String showScheduleDetails(@PathVariable Long eid,
                                      @RequestParam(required = false) String attendance,
                                      @RequestParam(required = false) String slot,
                                      @RequestParam(required = false) String dateDirection,
                                      @RequestParam(required = false) String slotDirection,
                                      Model model) {
        Enrollment enrollment;
        try {
            enrollment = enrollmentService.findById(eid);
        } catch (RuntimeException e) {
            return "redirect:/manager/schedules?error=enrollment_not_found";
        }

        // Apply only one sorting priority
        Sort sort = Sort.unsorted();
        if (dateDirection != null && !dateDirection.isEmpty()) {
            sort = Sort.by(Sort.Direction.fromString(dateDirection), "date");
        } else if (slotDirection != null && !slotDirection.isEmpty()) {
            sort = Sort.by(Sort.Direction.fromString(slotDirection), "slot");
        }

        Pageable pageable = PageRequest.of(0, 10, sort);

        Page<EnrollmentSchedule> schedulePage = enrollmentScheduleService.findScheduleByEnrollmentWithFilters(
                eid, attendance, slot, pageable
        );

        model.addAttribute("enrollment", enrollment);
        model.addAttribute("schedulePage", schedulePage);
        model.addAttribute("selectedAttendanceStatus", attendance);
        model.addAttribute("selectedSlot", slot);
        model.addAttribute("dateDirection", dateDirection);
        model.addAttribute("slotDirection", slotDirection);

        return "manager/schedule-details";
    }

    private LocalDateTime getStartDateByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "week":
                return now.minusDays(7);
            case "month":
                return now.minusDays(30);
            case "quarter":
                return now.minusDays(90);
            case "year":
                return now.minusDays(365);
            default:
                return now.minusDays(7);
        }
    }

    @GetMapping("/manager/mentor-working-date")
    public String showMentorWorkingDate(Model model,
                                        @RequestParam(defaultValue = "PENDING") String status) {
        MentorAvailableTime.Status enumValue = MentorAvailableTime.Status.valueOf(status);
        List<MentorAvailableTimeDTO> mentorRequest = mentorAvailableTimeService.findAllDistinctStartEndDates(enumValue);
        model.addAttribute("mentorRequest", mentorRequest);
        model.addAttribute("activeStatus", status.toUpperCase());
        return "manager/working-date-review";
    }

    @GetMapping("/manager/mentor-working-date/{mid}")
    public String showMentorWorkingDateView(Model model,
                                            @PathVariable String mid,
                                            @RequestParam LocalDate endLocal) {
        Mentor mentor = mentorService.getMentorById(mid);
        if (mentor == null) {
            return "redirect:/manager/mentor-working-date";
        }
        List<MentorAvailableSlotDTO> setSlots = mentorAvailableTimeService.findAllSlotByEndDate(mentor, endLocal);


        boolean[][] slotDayMatrix = mentorAvailableTimeService.slotDayMatrix(setSlots);
        model.addAttribute("slotDayMatrix", slotDayMatrix);
        model.addAttribute("mentor", mentor.getId());
        model.addAttribute("endDate", endLocal);
        model.addAttribute("slots", Slot.values());
        model.addAttribute("days", Day.values());
        return "manager/mentor-working-date";
    }

    @PostMapping("/manager/mentor-working-date/approve")
    public String approveMentorWorkingDate(@RequestParam LocalDate endDate,
                                           @RequestParam String mentor) {
        Mentor foundMentor = mentorService.getMentorById(mentor);
        if (mentor == null) {
            return "redirect:/manager/mentor-working-date";
        }
        List<MentorAvailableTime> mentorAvailableTimes = mentorAvailableTimeService.findAllMentorAvailableTimeByEndDate(foundMentor, endDate);
        for (MentorAvailableTime mentorAvailableTime : mentorAvailableTimes) {
            mentorAvailableTime.setStatus(MentorAvailableTime.Status.APPROVED);
        }
        mentorAvailableTimeService.insertWorkingSchedule(mentorAvailableTimes);
        mentorAvailableTimeService.insertMentorAvailableTime(mentorAvailableTimes.get(0).getId().getStartDate(), mentorAvailableTimes.get(0).getId().getEndDate(), foundMentor);
        return "redirect:/manager/mentor-working-date?approve=success";
    }

    @PostMapping("/manager/mentor-working-date/reject")
    public String rejectMentorWorkingDate(@RequestParam String reason,
                                          @RequestParam LocalDate endDate,
                                          @RequestParam String mentor) {
        Mentor foundMentor = mentorService.getMentorById(mentor);
        if (mentor == null) {
            return "redirect:/manager/mentor-working-date";
        }
        List<MentorAvailableTime> mentorAvailableTimes = mentorAvailableTimeService.findAllMentorAvailableTimeByEndDate(foundMentor, endDate);
        for (MentorAvailableTime mentorAvailableTime : mentorAvailableTimes) {
            mentorAvailableTime.setStatus(MentorAvailableTime.Status.REJECTED);
            mentorAvailableTime.setReason(reason);
        }
        mentorAvailableTimeService.insertWorkingSchedule(mentorAvailableTimes);
        return "redirect:/manager/mentor-working-date?approve=success";
    }


    @GetMapping("/manager/landing-page")
    public String showLandingPageEditor(
            @RequestParam(defaultValue = "GUEST") MenteeLandingRole role,
            @RequestParam(defaultValue = "hero") String activeTab,
            Model model
    ) {
        LandingPageConfig config = landingPageConfigService.getConfigByRole(role);
        model.addAttribute("landingConfig", config);
        model.addAttribute("role", role.name());
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("availableEndpoints", endpointRegistry.getGetEndpoints());
        model.addAttribute("isPersonalized", landingPageConfigService.isPersonalizationEnabled());
        return "manager/manage-landing-page";
    }

    @GetMapping("/manager/landing-page/reset")
    public String resetLandingPage(
            @RequestParam MenteeLandingRole role,
            @RequestParam(defaultValue = "hero") String activeTab,
            RedirectAttributes redirectAttributes) {
        LandingPageConfig currentConfig = landingPageConfigService.resetToCurrent(role);
        redirectAttributes.addFlashAttribute("landingConfig", currentConfig);
        redirectAttributes.addFlashAttribute("role", role.name());
        redirectAttributes.addFlashAttribute("resetSuccess", true);
        redirectAttributes.addFlashAttribute("isPersonalized", landingPageConfigService.isPersonalizationEnabled());
        return "redirect:/manager/landing-page?role=" + role.name() + "&activeTab=" + activeTab;
    }

    @PostMapping("/manager/landing-page/save")
    public String saveLandingPage(
            @Valid @ModelAttribute("landingConfig") LandingPageConfig landingConfig,
            BindingResult bindingResult,
            @RequestParam("role") MenteeLandingRole role,
            @RequestParam(defaultValue = "hero") String activeTab,
            @RequestParam(value = "heroImage", required = false) MultipartFile heroImage,
            @RequestParam(value = "categoryImage", required = false) MultipartFile categoryImage,
            @RequestParam(value = "aboutImage", required = false) MultipartFile aboutImage,
            @RequestParam(value = "courseImage", required = false) MultipartFile courseImage,
            @RequestParam(value = "mentorImage", required = false) MultipartFile mentorImage,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {

        if (bindingResult.hasErrors()) {
            String errorTab = determineErrorTab(bindingResult);
            if (errorTab != null) {
                activeTab = errorTab;
            }

            model.addAttribute("errorMessage", "Please fix the validation errors below.");
            model.addAttribute("role", role.name());
            model.addAttribute("activeTab", activeTab);
            model.addAttribute("availableEndpoints", endpointRegistry.getGetEndpoints());
            model.addAttribute("isPersonalized", landingPageConfigService.isPersonalizationEnabled());

            bindingResult.getFieldErrors().forEach(error ->
                    System.out.println("Validation error: " + error.getField() + " - " + error.getDefaultMessage())
            );

            return "manager/manage-landing-page";
        }

        try {
            LandingPageConfig originalConfig = landingPageConfigService.getConfigByRole(role);

            String heroImageUrl = landingPageConfigService.handleImageUpload(
                    heroImage, originalConfig.getHeroImageUrl()
            );
            String categoryImageUrl = landingPageConfigService.handleImageUpload(
                    categoryImage, originalConfig.getCategorySectionBgUrl()
            );
            String aboutImageUrl = landingPageConfigService.handleImageUpload(
                    aboutImage, originalConfig.getAboutSectionImageUrl()
            );
            String courseImageUrl = landingPageConfigService.handleImageUpload(
                    courseImage, originalConfig.getCourseSectionBgUrl()
            );
            String mentorImageUrl = landingPageConfigService.handleImageUpload(
                    mentorImage, originalConfig.getMentorSectionBgUrl()
            );

            landingConfig.setHeroImageUrl(heroImageUrl);
            landingConfig.setCategorySectionBgUrl(categoryImageUrl);
            landingConfig.setAboutSectionImageUrl(aboutImageUrl);
            landingConfig.setCourseSectionBgUrl(courseImageUrl);
            landingConfig.setMentorSectionBgUrl(mentorImageUrl);
            landingConfig.setId(originalConfig.getId());
            landingConfig.setRole(role);

            landingPageConfigService.save(landingConfig, role);
            redirectAttributes.addFlashAttribute("successMessage", "Landing page updated successfully!");
            redirectAttributes.addFlashAttribute("isPersonalized", landingPageConfigService.isPersonalizationEnabled());

            return "redirect:/manager/landing-page?role=" + role.name() + "&activeTab=" + activeTab;

        } catch (IOException e) {
            System.err.println("Image upload failed: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("isPersonalized", landingPageConfigService.isPersonalizationEnabled());

            return "redirect:/manager/landing-page?role=" + role.name() + "&activeTab=" + activeTab;

        } catch (Exception e) {
            System.err.println("Error saving landing page config: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("errorMessage", "Error saving configuration: " + e.getMessage());
            redirectAttributes.addFlashAttribute("isPersonalized", landingPageConfigService.isPersonalizationEnabled());

            return "redirect:/manager/landing-page?role=" + role.name() + "&activeTab=" + activeTab;
        }
    }

    @PostMapping("/manager/landing-page/preview")
    public String previewLandingPage(@ModelAttribute LandingPageConfig config,
                                     @RequestParam MenteeLandingRole role,
                                     @RequestParam(defaultValue = "hero") String activeTab,
                                     Model model) {

        // Hero Section
        model.addAttribute("heroHeadline", config.getHeroHeadline());
        model.addAttribute("heroSubHeadline", config.getHeroSubHeadline());
        model.addAttribute("heroCTA", config.getHeroCTA());
        model.addAttribute("heroCTALink", config.getHeroCTALink());
        model.addAttribute("heroImageUrl", config.getHeroImageUrl() != null ? config.getHeroImageUrl() : "/assets/images/default-hero.jpg");

        // Category Section
        model.addAttribute("categorySubtitle", config.getCategorySubtitle());
        model.addAttribute("categoryTitle", config.getCategoryTitle());
        model.addAttribute("categoryButtonText", config.getCategoryButtonText());
        model.addAttribute("categorySectionBgUrl", config.getCategorySectionBgUrl() != null ? config.getCategorySectionBgUrl() : "/assets/images/default-category.jpg");
        model.addAttribute("categorySectionSuggestion", config.getTagSuggestion());

        // About Section
        model.addAttribute("aboutSubtitle", config.getAboutSubtitle());
        model.addAttribute("aboutTitle", config.getAboutTitle());
        model.addAttribute("aboutDescription", config.getAboutDescription());
        model.addAttribute("aboutSectionImageUrl", config.getAboutSectionImageUrl() != null ? config.getAboutSectionImageUrl() : "/assets/images/default-about.jpg");

        // Course Sections
        model.addAttribute("sectionOneSubtitle", config.getSectionOneSubtitle());
        model.addAttribute("sectionOneTitle", config.getSectionOneTitle());
        model.addAttribute("sectionTwoSubtitle", config.getSectionTwoSubtitle());
        model.addAttribute("sectionTwoTitle", config.getSectionTwoTitle());
        model.addAttribute("courseSectionBgUrl", config.getCourseSectionBgUrl() != null ? config.getCourseSectionBgUrl() : "/assets/images/default-course.jpg");

        // Course suggestion logic
        model.addAttribute("courseSectionOneSuggestion", config.getCourseSectionOneSuggestion() != null ? config.getCourseSectionOneSuggestion() : "POPULAR");
        model.addAttribute("courseSectionTwoSuggestion", config.getCourseSectionTwoSuggestion() != null ? config.getCourseSectionTwoSuggestion() : "HISTORY_BASED");

        // Mentor Section
        model.addAttribute("mentorSectionTitle", config.getMentorSectionTitle() != null ? config.getMentorSectionTitle() : "Đội ngũ Mentor xuất sắc");
        model.addAttribute("mentorSectionSubtitle", config.getMentorSectionSubtitle());
        model.addAttribute("mentorSuggestion", config.getMentorSuggestion() != null ? config.getMentorSuggestion() : "RECOMMENDED");
        model.addAttribute("mentorSectionBgUrl", config.getMentorSectionBgUrl() != null ? config.getMentorSectionBgUrl() : "/assets/images/default-mentor.jpg");

        // Footer
        model.addAttribute("footerDescription", config.getFooterDescription());
        model.addAttribute("copyrightText", config.getCopyrightText());

        model.addAttribute("userType", role.name().toLowerCase());
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("role", role.name());
        return "manager/preview-landing-page";
    }

    @PostMapping("/manager/landing-page/toggle-personalization")
    public String togglePersonalization(@RequestParam boolean personalize,
                                        @RequestParam(defaultValue = "hero") String activeTab,
                                        RedirectAttributes redirectAttributes) {
        landingPageConfigService.setPersonalizationMode(personalize);
        if (!personalize) {
            landingPageConfigService.syncGuestConfigToAllRoles(personalize);
        }

        redirectAttributes.addFlashAttribute("isPersonalized", personalize);
        redirectAttributes.addFlashAttribute("successMessage", personalize
                ? "Personalized configs are now enabled per role."
                : "All roles now use GUEST config.");
        return "redirect:/manager/landing-page?activeTab=" + activeTab;
    }

    private String determineErrorTab(BindingResult bindingResult) {
        for (var error : bindingResult.getFieldErrors()) {
            String field = error.getField();

            if (field.startsWith("hero")) {
                return "hero";
            } else if (field.startsWith("category") || field.equals("tagSuggestion")) {
                return "categories";
            } else if (field.startsWith("about")) {
                return "about";
            } else if (field.startsWith("sectionOne") || field.startsWith("sectionTwo") ||
                    field.startsWith("courseSection")) {
                return "courses";
            } else if (field.startsWith("mentor")) {
                return "mentor";
            } else if (field.startsWith("footer") || field.equals("copyrightText")) {
                return "footer";
            }
        }
        return null;
    }
}