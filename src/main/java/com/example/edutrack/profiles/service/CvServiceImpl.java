package com.example.edutrack.profiles.service;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CvServiceImpl implements CvService {
    private final EntityManager entityManager;
    private final CvRepository cvRepository;
    private final UserRepository userRepository;

    @Autowired
    public CvServiceImpl(EntityManager entityManager, CvRepository cvRepository, UserRepository userRepository) {
        this.entityManager = entityManager;
        this.cvRepository = cvRepository;
        this.userRepository = userRepository;
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

        return cvRepository.save(cv);
    }

    @Override
    public CV getCVById(UUID id) {
        return cvRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cv not found with ID: " + id));
    }

    @Override
    public boolean acceptCV(UUID id) {
        Optional<CV> optionalCv = cvRepository.findById(id);
        if (optionalCv.isPresent()) {
            CV cv = optionalCv.get();
            if (cv.getStatus().equals("pending")) {
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
            if (cv.getStatus().equals("pending")) {
                cv.setStatus("rejected");
                cvRepository.save(cv);
                return true;
            }
        }
        return false;
    }
}
