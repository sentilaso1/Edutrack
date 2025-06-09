package com.example.edutrack.profiles.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.curriculum.model.CVCourse;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.repository.CVCourseRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CvServiceImpl implements CvService {
    private final EntityManager entityManager;
    private final CvRepository cvRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CVCourseRepository cvCourseRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public CvServiceImpl(EntityManager entityManager, CvRepository cvRepository, UserRepository userRepository, CourseRepository courseRepository, CVCourseRepository cvCourseRepository) {
        this.entityManager = entityManager;
        this.cvRepository = cvRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.cvCourseRepository = cvCourseRepository;
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
    public CV createCV(CVForm cvRequest) {
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

        if (cvRequest.getSelectedCourses() != null && !cvRequest.getSelectedCourses().isEmpty()) {
            String[] courseIds = cvRequest.getSelectedCourses().split(";");
            for (String courseIdStr : courseIds) {
                UUID courseId = UUID.fromString(courseIdStr);
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid course ID: " + courseId));
                CVCourse cvCourse = new CVCourse(cv, course);
                cvCourseRepository.save(cvCourse);
            }
        }

        return cv;
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
        String aiResponse = callMistralAPI(prompt);
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
        You are a CV validation assistant for a mentoring platform. Your task is to review the provided CV data and determine:
        1. Whether the skills and certifications are valid (i.e., relevant, realistic, and not fake, gibberish, or overly vague).
        2. Whether the overall CV is consistent and credible based on the summary, experience, education, and languages.

        Validation guidelines:
        - Skills should be specific, industry-relevant (e.g., "Java, Python, AWS" instead of "coding" or "expert").
        - Certifications should be from recognized institutions or platforms (e.g., AWS Certified Developer, Coursera, not "Supreme Certificate").
        - Check for inconsistencies (e.g., 20 years of experience for a 20-year-old candidate, or skills not matching experience).
        - If fields are empty or overly vague, consider them red flags unless justified by other sections.
        - Languages should be plausible and relevant to the mentoring context.
        

        Response requirements:
        - Respond ONLY with valid JSON in the exact format:
          {
            "is-approve": "true" or "false",
            "reason": "A concise explanation (1-2 sentences) of why the CV was approved or rejected."
          }
        - Ensure "is-approve" is a string ("true" or "false"), not a boolean.
        - Ensure "reason" is a non-empty string.
        - Do not include any explanation, markdown, code block, or extra fields. Output must be pure JSON only.
        - Escape special characters in the reason to ensure valid JSON.

        CV data:
        {
          "summary": "%s",
          "years of experience": "%s",
          "skills": "%s",
          "educations": "%s",
          "experience": "%s",
          "certifications": "%s",
          "languages": "%s"
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

    public String callMistralAPI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("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");

        Map<String, Object> body = Map.of(
                "model", "mistralai/devstral-small:free",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://openrouter.ai/api/v1/chat/completions",
                entity,
                String.class
        );

        return response.getBody();
    }

    public void processAIResponse(CV cv, String aiJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (aiJson == null || aiJson.trim().isEmpty()) {
                System.err.println("AI response is null or empty");
                return;
            }

            JsonNode root = mapper.readTree(aiJson);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                System.err.println("Invalid or empty choices array: " + aiJson);
                return;
            }

            JsonNode message = choices.get(0).path("message");
            if (message.isMissingNode()) {
                System.err.println("Missing message field: " + aiJson);
                return;
            }

            JsonNode contentNode = message.path("content");
            if (contentNode.isMissingNode() || !contentNode.isTextual()) {
                System.err.println("Missing or invalid content field: " + aiJson);
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
                System.err.println("Missing or invalid is-approve/reason fields: " + contentJson);
                return;
            }

            boolean approved = approveNode.asText().equalsIgnoreCase("true");
            String reason = reasonNode.asText();

            System.out.println("Result: " + aiJson);

            cv.setStatus(approved ? "aiapproved" : "rejected");
            cvRepository.save(cv);
        } catch (JsonProcessingException e) {
            System.err.println("JSON parsing error for AI response: " + aiJson);
        } catch (Exception e) {
            System.err.println("Unexpected error processing AI response: " + aiJson);
        }
    }
}
