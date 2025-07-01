package com.example.edutrack.profiles.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.common.service.interfaces.LLMService;
import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.CVCourse;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CVCourseRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.dto.CourseApplicationDetail;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CvServiceImpl implements CvService {
    private static final Logger logger = LoggerFactory.getLogger(CvServiceImpl.class);

    private final EntityManager entityManager;
    private final CvRepository cvRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CVCourseRepository cvCourseRepository;
    private final MentorRepository mentorRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final LLMService llmService;

    @Autowired
    public CvServiceImpl(EntityManager entityManager,
                         CvRepository cvRepository,
                         UserRepository userRepository,
                         CourseRepository courseRepository,
                         CVCourseRepository cvCourseRepository,
                         MentorRepository mentorRepository,
                         CourseMentorRepository courseMentorRepository,
                         LLMService llmService) {
        this.entityManager = entityManager;
        this.cvRepository = cvRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.cvCourseRepository = cvCourseRepository;
        this.mentorRepository = mentorRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.llmService = llmService;
    }

    @Override
    public List<CV> findAllCVs() {
        return cvRepository.findAll();
    }

    @Override
    public Page<CV> findAllCVsDateAsc(Pageable pageable) {
        return cvRepository.findAllStatusDateAsc(pageable);
    }

    @Override
    public Page<CV> findAllCVsDateDesc(Pageable pageable) {
        return cvRepository.findAllStatusDateDesc(pageable);
    }

    @Override
    public Page<CV> findAllCVsByStatusDateAsc(Pageable pageable, String status) {
        return cvRepository.findAllByStatusOrderByCreatedDateAsc(pageable, status);
    }

    @Override
    public Page<CV> findAllCVsByStatusDateDesc(Pageable pageable, String status) {
        return cvRepository.findAllByStatusOrderByCreatedDateDesc(pageable, status);
    }

    @Override
    public Page<CV> findAllCVsContainingSkills(List<String> skills, String sort, Pageable pageable) {
        if (skills == null || skills.isEmpty()) {
            return Page.empty(pageable);
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM cv WHERE ");
        List<String> clauses = new ArrayList<>();

        for (int i = 0; i < skills.size(); i++) {
            clauses.add("skills LIKE :skill" + i);
        }
        sql.append(String.join(" OR ", clauses));
        sql.append(" ORDER BY CASE status WHEN 'pending' THEN 1 WHEN 'approved' THEN 2 WHEN 'rejected' THEN 3 END, created_date ").append(sort != null && sort.equals(CVFilterForm.SORT_DATE_ASC) ? "ASC" : "DESC");

        Query query = entityManager.createNativeQuery(sql.toString(), CV.class);
        for (int i = 0; i < skills.size(); i++) {
            query.setParameter("skill" + i, "%" + skills.get(i) + "%");
        }

        // For pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<CV> resultList = query.getResultList();

        // Count query for total pages
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM cv WHERE ");
        countSql.append(String.join(" OR ", clauses));

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        for (int i = 0; i < skills.size(); i++) {
            countQuery.setParameter("skill" + i, "%" + skills.get(i) + "%");
        }

        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<CV> findAllCVsContainingSkillsByStatus(List<String> skills, String status, String sort, Pageable pageable) {
        if (skills == null || skills.isEmpty() || status == null) {
            return Page.empty(pageable);
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM cv WHERE status = :status AND (");
        List<String> skillClauses = new ArrayList<>();

        for (int i = 0; i < skills.size(); i++) {
            skillClauses.add("skills LIKE :skill" + i);
        }
        sql.append(String.join(" OR ", skillClauses)).append(")");
        sql.append(" ORDER BY CASE status WHEN 'pending' THEN 1 WHEN 'approved' THEN 2 WHEN 'rejected' THEN 3 END, created_date ").append(sort != null && sort.equals(CVFilterForm.SORT_DATE_ASC) ? "ASC" : "DESC");

        Query query = entityManager.createNativeQuery(sql.toString(), CV.class);
        query.setParameter("status", status);

        for (int i = 0; i < skills.size(); i++) {
            query.setParameter("skill" + i, "%" + skills.get(i) + "%");
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<CV> resultList = query.getResultList();

        // Count query
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM cv WHERE status = :status AND (");
        countSql.append(String.join(" OR ", skillClauses)).append(")");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        countQuery.setParameter("status", status);
        for (int i = 0; i < skills.size(); i++) {
            countQuery.setParameter("skill" + i, "%" + skills.get(i) + "%");
        }

        long total = ((Number) countQuery.getSingleResult()).longValue();
        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<CV> queryCVs(String filter, String sort, List<String> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            if (filter == null || filter.isEmpty()) {
                return queryAll(sort, pageable);
            }
            return queryByFilter(filter, sort, pageable);
        }

        if (filter == null || filter.isEmpty()) {
            return findAllCVsContainingSkills(tags, sort, pageable);
        }
        return findAllCVsContainingSkillsByStatus(tags, filter, sort, pageable);
    }

    private Page<CV> queryAll(String sort, Pageable pageable) {
        if (sort == null || sort.equals(CVFilterForm.SORT_DATE_DESC)) {
            return cvRepository.findAllStatusDateDesc(pageable);
        } else {
            return cvRepository.findAllStatusDateAsc(pageable);
        }
    }

    private Page<CV> queryByFilter(String filter, String sort, Pageable pageable) {
        if (sort == null || sort.equals(CVFilterForm.SORT_DATE_DESC)) {
            return cvRepository.findAllByStatusOrderByCreatedDateDesc(pageable, filter);
        } else {
            return cvRepository.findAllByStatusOrderByCreatedDateAsc(pageable, filter);
        }
    }

    @Override
    public List<String> getAllUniqueSkills() {
        List<String> skills = cvRepository.findAllSkills();

        List<String> distinctSkills = new ArrayList<>(skills.stream()
                .flatMap(skillSet -> Stream.of(skillSet.split(CV.ITEM_SEPARATOR_REGEX)))
                .distinct()
                .sorted()
                .toList());
        distinctSkills.replaceAll(String::trim);

        return distinctSkills;
    }

    @Override
    public List<Course> getCoursesForCV(CV cv) {
        return cvCourseRepository.findByIdCvId(cv.getId()).stream().map(CVCourse::getCourse).collect(Collectors.toList());
    }

    @Override
    public void createCV(CVForm cvRequest, UUID mentorId) {
        CV cv = validateEntitiesAndBuildCV(cvRequest, mentorId);
        cvRequest.parseSelectedCourses();
        validateAndApplyCourseDetails(cvRequest, cv, mentorId);
    }

    public CV validateEntitiesAndBuildCV(CVForm cvRequest, UUID mentorId) {
        User user = userRepository.findById(cvRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        CV cv = new CV(
                cvRequest.getSummary(),
                cvRequest.getExperienceYears(),
                cvRequest.getSkills(),
                cvRequest.getEducation(),
                user
        );
        cv.setId(user.getId());
        cv.setExperience(cvRequest.getExperience());
        cv.setCertifications(cvRequest.getCertifications());
        cv.setLanguages(cvRequest.getLanguages());
        cv.setPortfolioUrl(cvRequest.getPortfolioUrl());

        cvRepository.save(cv);

        return cv;
    }

    public void validateAndApplyCourseDetails(CVForm cvRequest, CV cv, UUID mentorId) {
        Map<UUID, CourseApplicationDetail> details = cvRequest.getCourseDetails();
        if (details == null || details.isEmpty()) return;

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        for (Map.Entry<UUID, CourseApplicationDetail> entry : details.entrySet()) {
            UUID courseId = entry.getKey();
            CourseApplicationDetail detail = entry.getValue();

            if (detail.getPrice() == null || detail.getPrice() <= 0) {
                throw new IllegalArgumentException("Invalid price for course: " + courseId);
            }
            if (detail.getDescription() == null || detail.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Missing description for course: " + courseId);
            }
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));

            handleCourseMentorLogic(cv, mentor, course, detail);
        }
    }

    public void handleCourseMentorLogic(CV cv, Mentor mentor, Course course, CourseApplicationDetail detail) {
        cvCourseRepository.save(new CVCourse(cv, course));

        Optional<CourseMentor> existingOpt = courseMentorRepository.findByMentorAndCourse(mentor, course);

        CourseMentor cm;
        if (existingOpt.isPresent()) {
            cm = existingOpt.get();
            if (cm.getStatus() != ApplicationStatus.REJECTED) {
                return;
            }
            cm.setPrice(detail.getPrice());
            cm.setDescription(detail.getDescription());
            cm.setStatus(ApplicationStatus.PENDING);
            cm.setAppliedDate(LocalDateTime.now());
        } else {
            cm = new CourseMentor();
            cm.setMentor(mentor);
            cm.setCourse(course);
            cm.setPrice(detail.getPrice());
            cm.setDescription(detail.getDescription());
            cm.setStatus(ApplicationStatus.PENDING);
            cm.setAppliedDate(LocalDateTime.now());
        }
        courseMentorRepository.save(cm);
    }



    @Override
    public CV getCVById(UUID id) {
        return cvRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cv not found with ID: " + id));
    }

    @Override
    public List<Course> getCoursesForCV(UUID cvId) {
        List<CVCourse> cvCourses = cvCourseRepository.findByIdCvId(cvId);
        return cvCourses.stream()
                .map(CVCourse::getCourse)
                .toList();
    }

    @Override
    public boolean acceptCV(UUID id) {
        Optional<CV> optionalCv = cvRepository.findById(id);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            if (cv.getStatus().equals("aiapproved")) {
                cv.setStatus("approved");
                cvRepository.save(cv);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean rejectCV(UUID id) {
        Optional<CV> optionalCv = cvRepository.findById(id);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            if (cv.getStatus().equals("aiapproved")) {
                cv.setStatus("rejected");
                cvRepository.save(cv);
                return true;
            }
        }
        return false;
    }

    @Override
    public void aiVerifyCV(CV cv) {
        String prompt = generatePrompt(cv);
        String aiResponse = llmService.callModel(prompt);
        processAIResponse(cv, aiResponse);
    }

    @Override
    public String generatePrompt(CV cv) {
        // Handle null or empty fields to prevent malformed JSON or prompt issues
        String summary = cv.getSummary() != null ? cv.getSummary() : "";
        String experienceYears = cv.getExperienceYears() != null ? cv.getExperienceYears().toString() : "0";
        String skills = cv.getSkills() != null ? cv.getSkills() : "";
        String education = cv.getEducation() != null ? cv.getEducation() : "";
        String experience = cv.getExperience() != null ? cv.getExperience() : "";
        String certifications = cv.getCertifications() != null ? cv.getCertifications() : "";
        String languages = cv.getLanguages() != null ? cv.getLanguages() : "";

        // Escape double quotes to prevent JSON injection or malformed strings
        summary = summary.replace("\"", "\\\"");
        skills = skills.replace("\"", "\\\"");
        education = education.replace("\"", "\\\"");
        experience = experience.replace("\"", "\\\"");
        certifications = certifications.replace("\"", "\\\"");
        languages = languages.replace("\"", "\\\"");


        return """
        You are an AI assistant responsible for validating CVs submitted to a mentoring platform. Your goal is to verify:
        1. Skills and Certifications:
            - Skills must be specific, clearly defined, and industry-relevant (e.g., "Java, Python, AWS" rather than "coding" or "expert").
            - Certifications must be genuine and issued by reputable institutions or recognized platforms (e.g., AWS Certified Developer, Coursera certifications).
        2. CV Consistency and Credibility:
            - Check for logical coherence across the summary, experience, education, certifications, and languages (e.g., the experience aligns with age and skills).
            - Flag inconsistencies such as improbable experience durations, unrealistic skills compared to stated experience, or incomplete/vague information.
            - Languages should be plausible and pertinent to mentoring.

        Validation Guidelines:
        - Treat empty or excessively vague fields as potential red flags unless clearly supported elsewhere in the CV.
        - Verify credibility, realism, and clarity explicitly.
        
        Response requirements:
        - You MUST reply ONLY with valid JSON (no markdown, no code block, no natural language, no explanation, no commentary, no  tags, no backticks). Return ONLY the following structure:
        - Provide responses strictly in valid JSON format:
          {
            "is-approve": "true" or "false",
            "reason": "A concise explanation (1-2 sentences) of why the CV was approved or rejected."
          }
        - Ensure "is-approve" is a string ("true" or "false"), not a boolean.
        - Ensure "reason" is a non-empty string.
        - Do not include any explanation, markdown, code block, or extra fields. Output must be pure JSON only.
        - Escape special characters in the reason to ensure valid JSON.
        - Do NOT add any extra text, explanation, or formatting. Do NOT wrap in backticks or code blocks.

        CV data:
        {
          "summary": "%s",
          "years of experience": "%s",
          "skills": "%s",
          "educations": "%s",
          "experience": "%s",
          "certifications": "%s",
          "languages": "%s",
        }

        Example valid responses:
        {
          "is-approve": "true",
          "reason": "Skills (Java, Python) and AWS Certified Developer certification are relevant and verifiable."
        }
        {
          "is-approve": "false",
          "reason": "Skills are vague ('expert') and certification ('Supreme Certificate') is not recognized."
        }
        Respond ONLY with raw JSON, no markdown, no explanation, no code block.
        """.formatted(summary, experienceYears, skills, education, experience, certifications, languages);
    }

    @Override
    public void processAIResponse(CV cv, String aiJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (aiJson == null || aiJson.trim().isEmpty()) {
                logger.warn("AI response is null or empty");
                return;
            }

            JsonNode root = mapper.readTree(aiJson);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                logger.error("Invalid or empty choices array: {}", aiJson);
                return;
            }

            JsonNode message = choices.get(0).path("message");
            if (message.isMissingNode()) {
                logger.error("Missing message field: {}", aiJson);
                return;
            }

            JsonNode contentNode = message.path("content");
            if (contentNode.isMissingNode() || !contentNode.isTextual()) {
                logger.error("Missing or invalid content field: {}", aiJson);
                return;
            }

            String contentJson = contentNode.asText();
            if (contentJson.startsWith("```")) {
                contentJson = contentJson.replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
            }
            JsonNode decision = mapper.readTree(contentJson);

            JsonNode approveNode = decision.get("is-approve");
            JsonNode reasonNode = decision.get("reason");
            if (approveNode == null || reasonNode == null || !approveNode.isTextual() || !reasonNode.isTextual()) {
                logger.error("Missing or invalid is-approve/reason fields: {}", contentJson);
                return;
            }

            boolean approved = approveNode.asText().equalsIgnoreCase("true");
            String reason = reasonNode.asText();

            if (!approved) {
                Mentor mentor = mentorRepository.findById(cv.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Mentor not found for user"));

                List<CourseMentor> mentorCourses = courseMentorRepository.findAllByMentor(mentor);
                for (CourseMentor cm : mentorCourses) {
                    cm.setStatus(ApplicationStatus.REJECTED);
                    courseMentorRepository.save(cm);
                }
                logger.info("CV rejected for mentorId {}: {}", cv.getId(), reason);
            } else {
                logger.info("CV approved for mentorId {}: {}", cv.getId(), reason);
            }

            logger.debug("AI response result: {}", aiJson);

            cv.setStatus(approved ? "aiapproved" : "rejected");
            cvRepository.save(cv);
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error for AI response: {}", aiJson, e);
        } catch (Exception e) {
            logger.error("Unexpected error processing AI response: {}", aiJson, e);
        }
    }

    private volatile boolean batchRunning = false;
    private volatile LocalDateTime lastBatchStart;
    private volatile LocalDateTime lastBatchEnd;

    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scheduleAIVerification() {
        batchRunning = true;
        lastBatchStart = LocalDateTime.now();
        try {
            List<CV> pendingCVs = cvRepository.findByStatus("pending");
            if (pendingCVs.isEmpty()) {
                return;
            }
            for (CV cv : pendingCVs) {
                aiVerifyCV(cv);
            }
        } finally {
            lastBatchEnd = LocalDateTime.now();
            batchRunning = false;
        }
    }

    public boolean isBatchRunning() { return batchRunning; }
    public LocalDateTime getLastBatchEnd() { return lastBatchEnd; }
}
