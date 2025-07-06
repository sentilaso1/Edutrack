package com.example.edutrack.timetables.specification;

import com.example.edutrack.timetables.model.Enrollment;
import org.springframework.data.jpa.domain.Specification;

public class EnrollmentSpecifications {

    public static Specification<Enrollment> searchMentee(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            return cb.like(cb.lower(root.get("mentee").get("fullName")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Enrollment> filterBySkill(String skillName) {
        return (root, query, cb) -> {
            if (skillName == null || skillName.isEmpty()) return null;
            return cb.equal(root.get("courseMentor").get("course").get("name"), skillName);
        };
    }
}

