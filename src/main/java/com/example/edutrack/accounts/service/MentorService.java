package com.example.edutrack.accounts.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.edutrack.accounts.dto.IncomeStatsDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;
    private final CourseMentorRepository courseMentorRepository;

    public MentorService(MentorRepository mentorRepository, CourseMentorRepository courseMentorRepository) {
        this.mentorRepository = mentorRepository;
        this.courseMentorRepository = courseMentorRepository;
    }

    // Receiving all mentors
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    // Filter mentors
    public Page<Mentor> searchMentors(String name, String[] expertise, Double rating, Integer totalSessions,
            Boolean isAvailable, Pageable pageable) {
        Page<Mentor> mentorPage = mentorRepository.searchMentorsBasic(name, rating, totalSessions, isAvailable,
                pageable);

        if (expertise != null && expertise.length > 0) {
            List<String> expertiseList = Arrays.stream(expertise)
                    .map(String::toLowerCase)
                    .toList();

            List<Mentor> filteredList = mentorPage.getContent().stream()
                    .filter(m -> {
                        String mentorExpertise = m.getExpertise().toLowerCase();
                        return expertiseList.stream().allMatch(mentorExpertise::contains);
                    })
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredList, pageable, mentorPage.getTotalElements());
        }

        return mentorPage;
    }

    public Optional<Mentor> getMentorById(UUID id) {
        return mentorRepository.findById(id);
    }

    public List<Course> getCoursesByMentor(UUID id) {
        return courseMentorRepository.findCoursesByMentorId(id);
    }

    public List<Tag> getTagsByMentor(UUID id) {
        return mentorRepository.findTagsByMentorId(id);
    }

    public Optional<CV> getCVById(UUID id) {
        return mentorRepository.findCVByMentorId(id);
    }

    public IncomeStatsDTO getIncomeStats(UUID mentorId) {
        // Lấy tổng thu nhập
        Long totalIncome = courseMentorRepository.getTotalIncomeByMentorId(mentorId);
        if (totalIncome == null)
            totalIncome = 0L;

        // Lấy thu nhập mỗi slot
        Long incomePerSlot = courseMentorRepository.getIncomePerSlot(mentorId);
        if (incomePerSlot == null)
            incomePerSlot = 0L;

        // Lấy thu nhập 12 tháng gần nhất
        List<Long> incomeOverTime = courseMentorRepository.getMonthlyIncomeList(mentorId);

        // Nếu không có dữ liệu, tạo list 12 tháng với giá trị 0
        if (incomeOverTime == null || incomeOverTime.isEmpty()) {
            incomeOverTime = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                incomeOverTime.add(0L);
            }
        }
        int previousMonth = (java.time.LocalDate.now().minusMonths(1).getMonthValue() - 1) % 12 + 1;
        int previousYear = java.time.LocalDate.now().minusMonths(1).getYear();

        Long incomeThisMonth = courseMentorRepository.getCurrentMonthIncome(mentorId);
        Long incomeLastMonth = courseMentorRepository.getLastMonthIncome(mentorId, previousMonth, previousYear);
        double percentChange = ((double) (incomeThisMonth - incomeLastMonth) / incomeLastMonth) * 100;
        if (Double.isNaN(percentChange) || Double.isInfinite(percentChange)) {
            percentChange = 0.0;
            
        }
        return new IncomeStatsDTO(totalIncome, incomeOverTime, incomePerSlot, percentChange);
    }
}
