package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.dto.TagEnrollmentCountDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import com.example.edutrack.curriculum.service.interfaces.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeControlller {
    private final CourseTagService courseTagService;
    private final EnrollmentService enrollmentService;
    private final CourseMentorService courseMentorService;
    private final MentorService mentorService;

    public HomeControlller(CourseTagService courseTagService, EnrollmentService enrollmentService, CourseMentorService courseMentorService, MentorService mentorService) {
        this.courseTagService = courseTagService;
        this.enrollmentService = enrollmentService;
        this.courseMentorService = courseMentorService;
        this.mentorService = mentorService;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return handleGuestUser(model);
        } else if (enrollmentService.getEnrollmentsByMenteeId(loggedInUser.getId()).isEmpty()) {
            return handleNewLoggedInUser(loggedInUser, model);
        } else {
            return handleExperiencedLoggedInUser(loggedInUser, model);
        }
    }

    private String handleExperiencedLoggedInUser(User loggedInUser, Model model) {
        return "mentee/mentee-landing-page";
    }

    private void addTopTagsToModel(Model model, int limit) {
        List<TagEnrollmentCountDTO> topTags = courseTagService.getTopTags(limit);
        model.addAttribute("topTags", topTags);
    }

    private String handleGuestUser(Model model) {
        model.addAttribute("headerCTA", "Sign Up");
        model.addAttribute("headerCTALink", "/signup");

        model.addAttribute("heroHeadline", "Better <span class=\"span\">Learning Future</span> Starts With Youdemi");
        model.addAttribute("heroSubHeadline", "It is long established fact that reader distracted by the readable content.");
        model.addAttribute("heroCTA", "Explore Courses");
        model.addAttribute("heroCTALink", "/courses");

        model.addAttribute("sectionOneTitle", "Featured Courses");
        model.addAttribute("sectionOneSubtitle", "Choose Unlimited Courses");

        model.addAttribute("sectionTwoTitle", "Latest Courses");
        model.addAttribute("sectionTwoSubtitle", "Newest Courses");

        addTopTagsToModel(model, 9);

        List<Tag> allCourseTags = courseTagService.getAllTags();
        List<Integer> allCourseTagIds = allCourseTags.stream().map(Tag::getId).toList();
        model.addAttribute("allCourseTagIds", allCourseTagIds);

        List<CourseMentor> popularCourses = enrollmentService.getPopularCoursesForGuest(8);
        List<CourseCardDTO> courseSectionOne = enrollmentService.mapToCourseCardDTOList(popularCourses);
        model.addAttribute("courseSectionOne", courseSectionOne);

        List<CourseMentor> latestCourses = courseMentorService.findLatestCourse(8);
        List<CourseCardDTO> courseSectionTwo = enrollmentService.mapToCourseCardDTOList(latestCourses);
        model.addAttribute("courseSectionTwo", courseSectionTwo);

        List<Mentor> topMentors = mentorService.getTopMentorsByRatingOrSessions(5);
        model.addAttribute("recommendedMentors", topMentors);

        model.addAttribute("userType", "guest");
        model.addAttribute("showSchedulesLink", false);

        return "mentee/mentee-landing-page";
    }


    private String handleNewLoggedInUser(User user, Model model) {
        model.addAttribute("headerCTA", "My Dashboard");
        model.addAttribute("headerCTALink", "/dashboard");

        model.addAttribute("heroHeadline", "Welcome, <span class=\"span\">" + user.getFullName() + "</span>!");
        model.addAttribute("heroSubHeadline", "Start your learning journey with personalized recommendations.");
        model.addAttribute("heroCTA", "Find Your First Course");
        model.addAttribute("heroCTALink", "/courses");

        model.addAttribute("sectionOneTitle", "Recommended For You");
        model.addAttribute("sectionOneSubtitle", "Based on your interests");

        List<CourseMentor> recommended = courseMentorService.getRecommendedCoursesByInterests(user.getId(), 8);
        List<CourseCardDTO> courseSectionOne = enrollmentService.mapToCourseCardDTOList(recommended);
        model.addAttribute("courseSectionOne", courseSectionOne);

        model.addAttribute("sectionTwoTitle", "Popular Now");
        model.addAttribute("sectionTwoSubtitle", "What other learners are exploring");

        addTopTagsToModel(model, 9);

        List<Tag> allCourseTags = courseTagService.getAllTags();
        List<Integer> allCourseTagIds = allCourseTags.stream().map(Tag::getId).toList();
        model.addAttribute("allCourseTagIds", allCourseTagIds);

        List<CourseMentor> popularCourses = enrollmentService.getPopularCoursesForGuest(8);
        List<CourseCardDTO> courseSectionTwo = enrollmentService.mapToCourseCardDTOList(popularCourses);
        model.addAttribute("courseSectionTwo", courseSectionTwo);

        List<Mentor> mentorsByInterest = mentorService.findMentorsByMenteeInterest(user.getId(), 5);
        model.addAttribute("recommendedMentors", mentorsByInterest);

        model.addAttribute("userType", "newUser");
        model.addAttribute("showSchedulesLink", false);

        return "mentee/mentee-landing-page";
    }




}
