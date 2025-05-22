package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.MentorAvailableTime;
import com.example.edutrack.curriculum.model.MentorAvailableTimeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MentorAvailableTimeRepository extends JpaRepository<MentorAvailableTime, MentorAvailableTimeId> {
    List<MentorAvailableTime> findByMentorId(UUID mentorId);
}
