package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.MentorAvailableTimeId;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MentorAvailableTimeRepository extends JpaRepository<MentorAvailableTime, MentorAvailableTimeId> {
    List<MentorAvailableTime> findByMentorId(UUID id);

    @Query("SELECT  COUNT(mat) > 0 FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND mat.id.slot = :slot " +
           "AND mat.id.startDate <= :startDate " +
           "AND mat.id.endDate >= :startDate " +
           "AND FUNCTION('WEEKDAY', :startDate) = mat.id.day")
    boolean isMentorAvailableTime(@Param("mentor") Mentor mentor,
                                  @Param("slot") Slot slot,
                                  @Param("startDate") LocalDate startDate);



    @Query("SELECT MAX(mat.id.endDate) FROM MentorAvailableTime mat WHERE mat.mentor = :mentor")
    LocalDate findMaxEndDate(Mentor mentor);

    @Query("SELECT MIN(mat.id.startDate) FROM MentorAvailableTime mat WHERE mat.mentor = :mentor")
    LocalDate findMinStartDate(Mentor mentor);

    @Query("SELECT DISTINCT new com.example.edutrack.timetables.dto.MentorAvailableTimeDTO(mat.id.startDate, mat.id.endDate) " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "ORDER BY mat.id.startDate DESC")
    List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(Mentor mentor);

    @Query("SELECT DISTINCT new com.example.edutrack.timetables.dto.MentorAvailableSlotDTO(mat.id.slot, mat.id.day) " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND mat.id.endDate = :endDate")
    List<MentorAvailableSlotDTO> findAllSlotByEndDate(Mentor mentor, LocalDate endDate);

}
