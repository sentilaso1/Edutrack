package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentorAvailableTimeRepository extends JpaRepository<MentorAvailableTime, MentorAvailableTimeId> {
    List<MentorAvailableTime> findByMentorId(UUID id);

    @Query("SELECT  COUNT(mat) > 0 FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND mat.id.slot = :slot " +
           "AND mat.id.startDate <= :startDate " +
           "AND mat.id.endDate >= :startDate " +
           "AND FUNCTION('WEEKDAY', :startDate) = mat.id.day " +
           "AND mat.status = 'APPROVED'")
    boolean isMentorAvailableTime(@Param("mentor") Mentor mentor,
                                  @Param("slot") Slot slot,
                                  @Param("startDate") LocalDate startDate);



    @Query("SELECT MAX(mat.date) FROM MentorAvailableTimeDetails mat WHERE mat.mentor = :mentor")
    LocalDate findMaxEndDate(Mentor mentor);

    @Query("SELECT MIN(mat.date) FROM MentorAvailableTimeDetails mat WHERE mat.mentor = :mentor AND mat.mentee is null ")
    LocalDate findMinStartDate(Mentor mentor);

    @Query("SELECT COUNT(mat) > 0 " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND  mat.id.endDate = :endDate " +
           "AND mat.status != :draft " +
           "AND mat.status != :reject ")
    Boolean isEndDateExisted(LocalDate endDate,
                             Mentor mentor,
                             MentorAvailableTime.Status draft,
                             MentorAvailableTime.Status reject);

    @Query("SELECT mat " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND  mat.id.endDate = :endDate " +
           "AND mat.status != :draft " +
           "AND mat.status != :reject ")
    List<MentorAvailableTime> findMentorAvailableTimeByStatus(LocalDate endDate,
                                                        Mentor mentor,
                                                        MentorAvailableTime.Status draft,
                                                        MentorAvailableTime.Status reject);

    @Query("SELECT DISTINCT new com.example.edutrack.timetables.dto.MentorAvailableTimeDTO(mat.id.startDate, mat.id.endDate, mat.mentor) " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor AND mat.status = :status " +
           "ORDER BY mat.id.startDate DESC")
    List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(Mentor mentor, MentorAvailableTime.Status status);

    @Query("SELECT DISTINCT new com.example.edutrack.timetables.dto.MentorAvailableSlotDTO(mat.id.slot, mat.id.day) " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND mat.id.endDate = :endDate")
    List<MentorAvailableSlotDTO> findAllSlotByEndDate(Mentor mentor, LocalDate endDate);

    @Query("SELECT DISTINCT new com.example.edutrack.timetables.dto.MentorAvailableTimeDTO(mat.id.startDate, mat.id.endDate, mat.mentor) " +
           "FROM MentorAvailableTime mat " +
           "WHERE mat.status = :status " +
           "ORDER BY mat.id.startDate DESC")
    List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(MentorAvailableTime.Status status);

    @Query("SELECT mat "+
           "FROM MentorAvailableTime mat " +
           "WHERE mat.mentor = :mentor " +
           "AND mat.id.endDate = :endDate " +
           "ORDER BY mat.id.endDate DESC ")
    List<MentorAvailableTime> findAllMentorAvailableTimeByEndDate(Mentor mentor, LocalDate endDate);

    @Query("SELECT MAX(mat.id.endDate) FROM MentorAvailableTime mat WHERE mat.mentor = :mentor AND mat.status = :enumValue")
    LocalDate findMaxEndDateByStatus(Mentor mentor, MentorAvailableTime.Status enumValue);

    @Query("SELECT mat FROM MentorAvailableTimeDetails mat WHERE mat.mentor.id = :mentorId " +
            "AND mat.date <= :endDate " +
            "AND mat.date >= :startDate " +
            "AND mat.mentee IS NOT NULL")
    List<MentorAvailableTimeDetails> findByMentorIdAndStatusAndDateRange(
            @Param("mentorId") UUID mentorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT new com.example.edutrack.timetables.dto.MentorAvailableSlotDTO(mat.id.slot, mat.id.day) " +
            "FROM MentorAvailableTime mat " +
            "WHERE mat.mentor = :mentor " +
            "AND mat.id.endDate = :endDate " +
            "AND mat.status = :status")
    List<MentorAvailableSlotDTO> findApprovedSlotsByEndDate(Mentor mentor, LocalDate endDate, MentorAvailableTime.Status status);

    @Query("SELECT MIN(mat.id.startDate) FROM MentorAvailableTime mat WHERE mat.mentor.id = :mentorId")
    Optional<LocalDate> findEarliestStartDateByMentorId(@Param("mentorId") UUID mentorId);
}
