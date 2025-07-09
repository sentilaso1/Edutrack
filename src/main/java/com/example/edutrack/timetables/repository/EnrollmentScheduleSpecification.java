package com.example.edutrack.timetables.repository;

import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Date;

public class EnrollmentScheduleSpecification {
    public static Specification<EnrollmentSchedule> byEnrollment(Long enrollmentId) {
        return (root, query, cb) -> cb.equal(root.get("enrollment").get("id"), enrollmentId);
    }

    public static Specification<EnrollmentSchedule> withAttendanceStatus(String attendanceStatus) {
        return (root, query, cb) -> {
            if (attendanceStatus == null || attendanceStatus.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("attendance"), EnrollmentSchedule.Attendance.valueOf(attendanceStatus)); // Fixed from "attendanceStatus"
        };
    }

    public static Specification<EnrollmentSchedule> withSlot(String slotName) {
        return (root, query, cb) -> {
            if (slotName == null || slotName.isEmpty()) {
                return cb.conjunction();
            }

            try {
                Slot slotEnum = Slot.valueOf(slotName); // Will throw if invalid
                return cb.equal(root.get("slot"), slotEnum);
            } catch (IllegalArgumentException ex) {
                return cb.conjunction(); // Fail-safe: treat as "no filter"
            }
        };
    }

    public static Specification<EnrollmentSchedule> withinLast7Days() {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysAgo = now.minusDays(7);
            return cb.between(root.get("date"), sevenDaysAgo, now);
        };
    }

}
