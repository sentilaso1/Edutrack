package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.timetables.dto.EnrollmentAttendanceDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EnrollmentScheduleRepositoryImpl implements EnrollmentScheduleRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    private final MenteeRepository menteeRepository;
    private final CourseMentorRepository courseMentorRepository;

    public EnrollmentScheduleRepositoryImpl(EntityManager entityManager,
                                            MenteeRepository menteeRepository,
                                            CourseMentorRepository courseMentorRepository) {
        this.entityManager = entityManager;
        this.menteeRepository = menteeRepository;
        this.courseMentorRepository = courseMentorRepository;
    }

    @Override
    public Page<EnrollmentAttendanceDTO> findScheduleToBeConfirmedFiltered(Pageable pageable, String menteeId, String mentorId) {
        String baseQuery = """
            SELECT e.id, BIN_TO_UUID(e.mentee_id), BIN_TO_UUID(e.course_mentor_id)
            FROM enrollments e
            JOIN enrollment_schedule es ON e.id = es.enrollment_id
            JOIN course_mentor cm ON cm.id = e.course_mentor_id
            WHERE es.date BETWEEN NOW() - INTERVAL 7 DAY AND NOW()
        """;

        String countQuery = """
            SELECT COUNT(DISTINCT e.id)
            FROM enrollments e
            JOIN enrollment_schedule es ON e.id = es.enrollment_id
            JOIN course_mentor cm ON cm.id = e.course_mentor_id
            WHERE es.date BETWEEN NOW() - INTERVAL 7 DAY AND NOW()
        """;

        Map<String, Object> params = new HashMap<>();

        if (menteeId != null && !menteeId.isBlank()) {
            baseQuery += " AND BIN_TO_UUID(e.mentee_id) = :menteeId";
            countQuery += " AND BIN_TO_UUID(e.mentee_id) = :menteeId";
            params.put("menteeId", menteeId);
        }

        if (mentorId != null && !mentorId.isBlank()) {
            baseQuery += " AND BIN_TO_UUID(cm.mentor_user_id) = :mentorId";
            countQuery += " AND BIN_TO_UUID(cm.mentor_user_id) = :mentorId";
            params.put("mentorId", mentorId);
        }

        baseQuery += " GROUP BY e.id ORDER BY e.id DESC";

        Query query = entityManager.createNativeQuery(baseQuery);
        Query count = entityManager.createNativeQuery(countQuery);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
            count.setParameter(entry.getKey(), entry.getValue());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<EnrollmentAttendanceDTO> results = rows.stream().map(row -> {
            Long id = ((Number) row[0]).longValue();
            String menteeIdStr = (String) row[1];
            String courseMentorIdStr = (String) row[2];

            Optional<Mentee> menteeOpt = menteeRepository.findById(UUID.fromString(menteeIdStr));
            CourseMentor courseMentor = courseMentorRepository.findById(UUID.fromString(courseMentorIdStr));

            return menteeOpt.map(mentee -> new EnrollmentAttendanceDTO(id, mentee, courseMentor)).orElse(null);
        }).filter(Objects::nonNull).toList();

        long total = ((Number) count.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }
}
