package com.example.edutrack.accounts.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.edutrack.accounts.dto.IncomeStatsDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final CvRepository cvRepository;

    public MentorService(MentorRepository mentorRepository,
                         CourseMentorRepository courseMentorRepository,
                         CvRepository cvRepository) {
        this.mentorRepository = mentorRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.cvRepository = cvRepository;
    }

    // Receiving all mentors
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
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

    public List<String> getAllMentorSkills() {
        List<Mentor> allMentors = mentorRepository.findAll();
        Set<String> allSkillsSet = new HashSet<>();
        for (Mentor mentor : allMentors) {
            if (mentor.getExpertise() != null) {
                String[] skillsArr = mentor.getExpertise().split(",");
                for (String skill : skillsArr) {
                    String trimmed = skill.trim();
                    if (!trimmed.isEmpty()) allSkillsSet.add(trimmed);
                }
            }
        }
        return new ArrayList<>(allSkillsSet);
    }

    public Page<Mentor> searchMentorsWithApprovedCV(
            String name, String[] expertise, Double rating,
            Integer totalSessions, Boolean isAvailable, Pageable pageable
    ) {
        // Expertise filtering can be done after fetching, or in JPQL if needed
        Page<Mentor> page = mentorRepository.searchMentorsWithApprovedCV(
                name,
                rating,
                totalSessions,
                isAvailable,
                CV.STATUS_APPROVED,
                pageable
        );

        // If you need to filter further by expertise
        if (expertise != null && expertise.length > 0 && !expertise[0].isEmpty()) {
            List<String> expertiseList = Arrays.stream(expertise)
                    .map(String::trim)
                    .filter(e -> !e.isEmpty())
                    .map(String::toLowerCase)
                    .toList();
            List<Mentor> filtered = page.getContent().stream()
                    .filter(m -> m.getExpertiseItem().stream()
                            .map(String::toLowerCase)
                            .anyMatch(expertiseList::contains))
                    .toList();
            return new PageImpl<>(filtered, pageable, filtered.size());
        }
        return page;
    }

    public List<String> getAllMentorExpertiseFromApprovedCVs() {
        List<CV> approvedCVs = cvRepository.findByStatus(CV.STATUS_APPROVED);
        Set<String> expertiseSet = new HashSet<>();
        for (CV cv : approvedCVs) {
            User user = cv.getUser();
            if (user != null) {
                Optional<Mentor> mentorOpt = mentorRepository.findById(user.getId());
                if (mentorOpt.isPresent()) {
                    Mentor mentor = mentorOpt.get();
                    List<String> expertiseItems = mentor.getExpertiseItem();
                    expertiseSet.addAll(expertiseItems);
                }
            }
        }
        return new ArrayList<>(expertiseSet);
    }
}
