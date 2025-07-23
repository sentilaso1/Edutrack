package com.example.edutrack.profiles.service.implementations;

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
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
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
    private final CourseMentorService courseMentorService;

    @Autowired
    public CvServiceImpl(EntityManager entityManager,
                         CvRepository cvRepository,
                         UserRepository userRepository,
                         CourseRepository courseRepository,
                         CVCourseRepository cvCourseRepository,
                         MentorRepository mentorRepository,
                         CourseMentorRepository courseMentorRepository,
                         LLMService llmService,
                         CourseMentorService courseMentorService) {
        this.entityManager = entityManager;
        this.cvRepository = cvRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.cvCourseRepository = cvCourseRepository;
        this.mentorRepository = mentorRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.llmService = llmService;
        this.courseMentorService = courseMentorService;
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

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<CV> resultList = query.getResultList();

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
        logger.warn("Enter create CV method.");
        CV cv = validateEntitiesAndBuildCV(cvRequest);
        validateAndApplyCourseDetails(cvRequest, cv, mentorId);
    }

    public CV validateEntitiesAndBuildCV(CVForm cvRequest) {
        logger.warn("Enter validateEntitiesAndBuildCV method.");
        User user = userRepository.findById(cvRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        logger.warn("Enter validateAndApplyCourseDetails method.");
        List<CourseApplicationDetail> details = cvRequest.getCourseDetails();
        if (details == null || details.isEmpty()) return;

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        Set<UUID> newCourseIds = details.stream()
                .map(CourseApplicationDetail::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<CourseMentor> oldMentorCourses = courseMentorRepository.findByMentorId(mentorId);
        for (CourseMentor old : oldMentorCourses) {
            if (!newCourseIds.contains(old.getCourse().getId())) {
                courseMentorRepository.delete(old);
            }
        }

        List<CVCourse> oldCVCourses = cvCourseRepository.findByIdCvId(cv.getId());
        cvCourseRepository.deleteAll(oldCVCourses);

        for (CourseApplicationDetail form : details) {
            logger.warn("CourseApplication courseId: {}", form.getCourseId());
            UUID courseId = form.getCourseId();
            if (courseId == null){
                logger.warn("Skipping course with null courseId");
                continue;
            }

            String desc = form.getDescription();
            if (desc == null || desc.trim().isEmpty()) {
                throw new IllegalArgumentException("Missing description for course: " + courseId);
            }

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));

            handleCourseMentorLogic(cv, mentor, course, form);
        }
    }


    public void handleCourseMentorLogic(CV cv, Mentor mentor, Course course, CourseApplicationDetail detail) {
        logger.debug("Entering handleCourseMentorLogic for courseId={}", detail.getCourseId());
        cvCourseRepository.save(new CVCourse(cv, course));
        logger.info("Course ID: {}, Description: {}", detail.getCourseId(), detail.getDescription());
        Optional<CourseMentor> existing = courseMentorRepository.findByMentorAndCourse(mentor, course);

        CourseMentor cm = existing.orElse(new CourseMentor());
        cm.setMentor(mentor);
        cm.setCourse(course);
        cm.setDescription(detail.getDescription());
        cm.setStatus(ApplicationStatus.PENDING);

        courseMentorRepository.save(cm);

    }

    @Override
    public CV getCVById(UUID id) {
        return cvRepository.findById(id).orElse(null);
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
        logger.warn("Optional CV: {}", optionalCv);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            cv.setStatus("approved");
            cvRepository.save(cv);
            List<CourseMentor> courseMentor = courseMentorService.findByMentorId(id);

            for (CourseMentor cm : courseMentor) {
                cm.setStatus(ApplicationStatus.ACCEPTED);
                courseMentorService.save(cm);
            }

            String aiResponse = aiProcessCV(cv);
            if (aiResponse != null && !aiResponse.isEmpty()) {
                aiFormatCV(cv, aiResponse);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean rejectCV(UUID id) {
        Optional<CV> optionalCv = cvRepository.findById(id);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            cv.setStatus("rejected");
            cvRepository.save(cv);
            List<CourseMentor> courseMentor = courseMentorService.findByMentorId(id);

            for (CourseMentor cm : courseMentor) {
                cm.setStatus(ApplicationStatus.REJECTED);
                courseMentorService.save(cm);
            }
            return true;
        }

        return false;
    }

    @Override
    public String aiProcessCV(CV cv) {
        String prompt = generateCombinedPromptForAI(cv);
        return llmService.callModel(prompt);
    }

    @Override
    public void aiVerifyCV(CV cv, String aiResponse) {
        processVerificationFromResponse(cv, aiResponse);
    }

    @Override
    public void aiFormatCV(CV cv, String aiResponse) {
        processFormattingFromResponse(cv, aiResponse);
    }

    @Override
    public String generateCombinedPromptForAI(CV cv) {
        // Common field extraction and escaping
        String skills = cv.getSkills() != null ? cv.getSkills() : "";
        String education = cv.getEducation() != null ? cv.getEducation() : "";
        String certifications = cv.getCertifications() != null ? cv.getCertifications() : "";
        String experience = cv.getExperience() != null ? cv.getExperience() : "";
        String languages = cv.getLanguages() != null ? cv.getLanguages() : "";
        String summary = cv.getSummary() != null ? cv.getSummary() : "";
        String experienceYears = cv.getExperienceYears() != null ? cv.getExperienceYears().toString() : "0";

        // Escape quotes for all fields
        skills = skills.replace("\"", "\\\"");
        education = education.replace("\"", "\\\"");
        certifications = certifications.replace("\"", "\\\"");
        experience = experience.replace("\"", "\\\"");
        languages = languages.replace("\"", "\\\"");
        summary = summary.replace("\"", "\\\"");

        return """
        You are an AI assistant responsible for both validating and formatting CV data. You must perform two tasks simultaneously:
        
        TASK 1 - VALIDATION:
        Verify the CV for:
        1. Skills and Certifications:
            - Skills must be specific, clearly defined, and industry-relevant (e.g., "Java, Python, AWS" rather than "coding" or "expert").
            - Certifications must be genuine and issued by reputable institutions or recognized platforms (e.g., AWS Certified Developer, Coursera certifications).
        2. CV Consistency and Credibility:
            - Check for logical coherence across the summary, experience, education, certifications, and languages (e.g., the experience aligns with age and skills).
            - Flag inconsistencies such as improbable experience durations, unrealistic skills compared to stated experience, or incomplete/vague information.
            - Languages should be plausible and pertinent to mentoring.
        
        TASK 2 - FORMATTING:
        Format all CV data fields with these rules:
        1. Header and Section Recognition:
            - Identify and REMOVE common section headers/labels such as:
              • "Technical Skills:", "Soft Skills:", "Programming Languages:", "Core Competencies:"
              • "Education Background:", "Academic Qualifications:", "Degrees:"
              • "Certifications Earned:", "Professional Certifications:", "Licenses:"
              • "Work Experience:", "Professional Experience:", "Employment History:"
              • "Languages Spoken:", "Language Proficiency:", "Foreign Languages:"
            - Remove organizational markers like:
              • Bullet points (•, *, -, →, ►)
              • Numbering (1., 2., a), b), i., ii.)
              • Indentation markers
              • Category dividers or subsection labels
            - Preserve only the actual content items (skill names, degree titles, company names, etc.)
        
        2. Separator Recognition and Normalization:
            - Identify all types of separators used in the input (commas, dashes, line breaks, multiple spaces, tabs, etc.)
            - Convert all separators to semicolons (;)
            - Preserve the actual skill/item names while standardizing the separation
        
        3. Data Cleaning:
            - Remove excessive whitespace while preserving item names EXACTLY as written
            - Handle mixed separator patterns (e.g., "Python - Java, SQL")
            - Eliminate empty entries caused by multiple consecutive separators
            - NEVER change spelling, capitalization, or wording - preserve ALL text exactly as provided
            - Remove only organizational/header text, not actual content
        
        4. Output Consistency:
            - Each item should be separated by exactly one semicolon
            - No leading or trailing semicolons
            - No spaces around semicolons unless part of the item name
            - Preserve multi-word items exactly (e.g., "Spring Boot", "Machine Learning")
            - CRITICAL: Never modify, correct, or change any text content - only remove headers and change separators
        
        Formatting Guidelines:
        - Treat any non-alphanumeric characters (except spaces within item names) as potential separators
        - Recognize line breaks, multiple spaces, tabs as separators
        - Handle mixed patterns intelligently (commas AND dashes AND line breaks)
        - Empty or whitespace-only inputs should return empty strings
        - ABSOLUTELY CRITICAL: You are ONLY a formatter for the data fields - do NOT change any text content, spelling, or wording
        - Even if text appears misspelled (e.g., "Puthon" instead of "Python"), preserve it exactly as written
        - Your job is to: 1) Remove headers/organizational text, 2) Identify separators and replace them with semicolons
        
        Response requirements:
        - You MUST reply ONLY with valid JSON (no markdown, no code block, no natural language, no explanation, no commentary, no tags, no backticks).
        - Provide responses strictly in valid JSON format:
          {
            "validation": {
              "is-approve": "true" or "false",
              "reason": "A concise explanation (1-2 sentences) of why the CV was approved or rejected."
            },
            "formatted": {
              "skills": "formatted_skills_string",
              "education": "formatted_education_string", 
              "certifications": "formatted_certifications_string",
              "experience": "formatted_experience_string",
              "languages": "formatted_languages_string"
            }
          }
        - Ensure "is-approve" is a string ("true" or "false"), not a boolean.
        - Ensure "reason" is a non-empty string.
        - Ensure all formatted fields are strings with semicolon-separated values or empty strings.
        - Do not include any explanation, markdown, code block, or extra fields. Output must be pure JSON only.
        - Escape special characters to ensure valid JSON.
        - Do NOT add any extra text, explanation, or formatting. Do NOT wrap in backticks or code blocks.
        
        CV data:
        {
          "summary": "%s",
          "years of experience": "%s",
          "skills": "%s",
          "education": "%s",
          "experience": "%s",
          "certifications": "%s",
          "languages": "%s"
        }
        
        Example valid response:
        {
          "validation": {
            "is-approve": "true",
            "reason": "Skills are specific and relevant, certifications are from recognized institutions."
          },
          "formatted": {
            "skills": "Java;Python;AWS;Spring Boot",
            "education": "Bachelor of Computer Science;Master of Data Science",
            "certifications": "AWS Certified Developer;Google Cloud Professional",
            "experience": "Senior Developer at TechCorp;Junior Analyst at DataFirm",
            "languages": "English;Vietnamese;French"
          }
        }
        
        Respond ONLY with raw JSON, no markdown, no explanation, no code block.
        """.formatted(summary, experienceYears, skills, education, experience, certifications, languages);
    }

    private void processVerificationFromResponse(CV cv, String aiJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (aiJson == null || aiJson.trim().isEmpty()) {
                logger.warn("AI response is null or empty for verification");
                return;
            }

            JsonNode root = mapper.readTree(aiJson);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                logger.error("Invalid or empty choices array for verification: {}", aiJson);
                return;
            }

            JsonNode message = choices.get(0).path("message");
            if (message.isMissingNode()) {
                logger.error("Missing message field for verification: {}", aiJson);
                return;
            }

            JsonNode contentNode = message.path("content");
            if (contentNode.isMissingNode() || !contentNode.isTextual()) {
                logger.error("Missing or invalid content field for verification: {}", aiJson);
                return;
            }

            String contentJson = contentNode.asText();
            if (contentJson.startsWith("```")) {
                contentJson = contentJson.replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
            }

            JsonNode responseData = mapper.readTree(contentJson);

            JsonNode validationNode = responseData.path("validation");
            if (!validationNode.isMissingNode()) {
                processVerificationResponse(cv, validationNode, contentJson);
                logger.info("Verification processed from existing AI response for CV {}", cv.getId());
            } else {
                logger.warn("Missing validation data in AI response for verification processing");
            }

        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error for verification from response: {}", aiJson, e);
        } catch (Exception e) {
            logger.error("Unexpected error processing verification from response: {}", aiJson, e);
            throw new RuntimeException("Verification processing error", e);
        }
    }

    private void processVerificationResponse(CV cv, JsonNode responseData, String contentJson) {
        JsonNode approveNode = responseData.get("is-approve");
        JsonNode reasonNode = responseData.get("reason");

        if (approveNode == null || reasonNode == null || !approveNode.isTextual() || !reasonNode.isTextual()) {
            logger.error("Missing or invalid is-approve/reason fields: {}", contentJson);
            return;
        }

        boolean approved = approveNode.asText().equalsIgnoreCase("true");
        String reason = reasonNode.asText();

        if (!approved) {
            rejectCV(cv.getId());
            logger.info("CV rejected for mentorId {}: {}", cv.getId(), reason);
        } else {
            logger.info("CV approved for mentorId {}: {}", cv.getId(), reason);
        }

        cv.setStatus(approved ? "aiapproved" : "rejected");
        cvRepository.save(cv);
    }

    private void processFormattingFromResponse(CV cv, String aiJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (aiJson == null || aiJson.trim().isEmpty()) {
                logger.warn("AI response is null or empty for formatting");
                return;
            }

            JsonNode root = mapper.readTree(aiJson);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                logger.error("Invalid or empty choices array for formatting: {}", aiJson);
                return;
            }

            JsonNode message = choices.get(0).path("message");
            if (message.isMissingNode()) {
                logger.error("Missing message field for formatting: {}", aiJson);
                return;
            }

            JsonNode contentNode = message.path("content");
            if (contentNode.isMissingNode() || !contentNode.isTextual()) {
                logger.error("Missing or invalid content field for formatting: {}", aiJson);
                return;
            }

            String contentJson = contentNode.asText();
            if (contentJson.startsWith("```")) {
                contentJson = contentJson.replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
            }

            JsonNode responseData = mapper.readTree(contentJson);

            JsonNode formattedNode = responseData.path("formatted");
            if (!formattedNode.isMissingNode()) {
                processFormattingResponse(cv, formattedNode, contentJson);
                logger.info("Formatting processed from existing AI response for CV {}", cv.getId());
            } else {
                logger.warn("Missing formatted data in AI response for formatting processing");
            }

        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error for formatting from response: {}", aiJson, e);
        } catch (Exception e) {
            logger.error("Unexpected error processing formatting from response: {}", aiJson, e);
            throw new RuntimeException("Formatting processing error", e);
        }
    }

    private void processFormattingResponse(CV cv, JsonNode responseData, String contentJson) {
        JsonNode skillsNode = responseData.get("skills");
        JsonNode educationNode = responseData.get("education");
        JsonNode certificationsNode = responseData.get("certifications");
        JsonNode experienceNode = responseData.get("experience");
        JsonNode languagesNode = responseData.get("languages");

        if (skillsNode == null || educationNode == null || certificationsNode == null ||
                experienceNode == null || languagesNode == null ||
                !skillsNode.isTextual() || !educationNode.isTextual() || !certificationsNode.isTextual() ||
                !experienceNode.isTextual() || !languagesNode.isTextual()) {
            logger.error("Missing or invalid formatting fields: {}", contentJson);
            return;
        }

        cv.setSkills(skillsNode.asText());
        cv.setEducation(educationNode.asText());
        cv.setCertifications(certificationsNode.asText());
        cv.setExperience(experienceNode.asText());
        cv.setLanguages(languagesNode.asText());

        cvRepository.save(cv);
        logger.info("CV formatting completed for mentorId {}", cv.getId());
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
                String aiResponse = aiProcessCV(cv);
                aiVerifyCV(cv, aiResponse);
                aiFormatCV(cv, aiResponse);
            }
        } finally {
            lastBatchEnd = LocalDateTime.now();
            batchRunning = false;
        }
    }

    public boolean isBatchRunning() { return batchRunning; }
    public LocalDateTime getLastBatchEnd() { return lastBatchEnd; }

    public CV getCVByMentorId(UUID id) {
        return cvRepository.findByMentorId(id);
    }
}
