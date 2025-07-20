package com.example.edutrack.accounts.service.implementations;

import java.util.*;

import com.example.edutrack.accounts.dto.IncomeStatsDTO;
import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.edutrack.accounts.model.Mentor;

@Service
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final CvRepository cvRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MentorServiceImpl(MentorRepository mentorRepository,
                             MenteeRepository menteeRepository,
                             CourseMentorRepository courseMentorRepository,
                             CvRepository cvRepository) {
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.cvRepository = cvRepository;
    }

    public Mentor getMentorById(String id) {
        try {
            return mentorRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mentor not found with id: " + id));
        } catch (IllegalArgumentException e) {
            return null;
        }

    }


    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }


    public Optional<Mentor> findById(UUID id) {
        return mentorRepository.findById(id);
    }

    @Override
    public long countAll() {
        return mentorRepository.count();
    }

    @Override
    public List<Mentor> findAll() {
        return mentorRepository.findAll();
    }

    @Override
    public List<Mentor> getTopMentorsByRatingOrSessions(int limit) {
        return mentorRepository.findTopActiveMentors(PageRequest.of(0, limit));
    }

    @Override
    public List<Mentor> findMentorsByMenteeInterest(UUID menteeId, int limit){
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("Mentee not found"));
        List<String> interests = Arrays.stream(mentee.getInterests().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        List<Mentor> mentorList = new ArrayList<>();
        Set<UUID> seen = new HashSet<>();

        for(String keyword : interests){
            List<Mentor> mentorWithKeyword = mentorRepository.findByExpertiseKeyword(keyword, PageRequest.of(0, limit));
            for (Mentor mentor : mentorWithKeyword){
                if(seen.add(mentor.getId())){
                    mentorList.add(mentor);
                    if (mentorList.size() >= limit) return mentorList;
                }
            }

        }
        if (mentorList.size() < limit) {
            int remaining = limit - mentorList.size();
            List<Mentor> topMentors = mentorRepository.findTopActiveMentors(PageRequest.of(0, remaining));
            for (Mentor mentor : topMentors) {
                if (seen.add(mentor.getId())) {
                    mentorList.add(mentor);
                    if (mentorList.size() >= limit) {
                        break;
                    }
                }
            }
        }
        return mentorList;

    }

    @Transactional
    @Override
    public Optional<Mentor> promoteToMentor(UUID userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            return Optional.empty();
        }

        Mentor mentor = new Mentor();
        mentor.setTotalSessions(0);
        mentor.setAvailable(false);
        mentor.setExpertise(null);
        mentor.setRating(null);

        mentor.setId(userId);
        return Optional.ofNullable(entityManager.merge(mentor));
    }

    @Override
    public List<CourseMentor> getCourseMentorRelations(UUID mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
        return courseMentorRepository.findAllByMentor(mentor);
    }

    @Override
    public Page<Mentor> searchMentorsWithApprovedCV(
            String name, String[] expertise, Double rating,
            Integer totalSessions, Boolean isAvailable, Pageable pageable
    ) {
        Page<Mentor> page = mentorRepository.searchMentorsWithApprovedCV(
                name,
                rating,
                totalSessions,
                isAvailable,
                CV.STATUS_APPROVED,
                Pageable.unpaged()
        );

        if (expertise != null && expertise.length > 0 && !expertise[0].isEmpty()) {
            List<String> expertiseList = Arrays.stream(expertise)
                    .map(String::trim)
                    .filter(e -> !e.isEmpty())
                    .map(String::toLowerCase)
                    .toList();

            List<Mentor> filtered = page.getContent().stream()
                    .filter(m -> m.getExpertiseItem() != null &&
                            m.getExpertiseItem().stream()
                                    .map(s -> s.trim().toLowerCase())
                                    .anyMatch(expertiseList::contains))
                    .toList();

            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber();
            int startItem = currentPage * pageSize;
            int endItem = Math.min(startItem + pageSize, filtered.size());

            List<Mentor> pagedMentors = startItem < filtered.size()
                    ? filtered.subList(startItem, endItem)
                    : Collections.emptyList();

            return new PageImpl<>(pagedMentors, pageable, filtered.size());
        }
        return mentorRepository.searchMentorsWithApprovedCV(
                name, rating, totalSessions, isAvailable,
                CV.STATUS_APPROVED, pageable
        );
    }

    @Override
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
                    if (expertiseItems != null) {
                        expertiseSet.addAll(expertiseItems);
                    }
                }
            }
        }
        return new ArrayList<>(expertiseSet);
    }

    @Override
    public Optional<Mentor> getMentorById(UUID id) {
        return mentorRepository.findById(id);
    }

    @Override
    public List<Course> getCoursesByMentor(UUID id) {
        return courseMentorRepository.findCoursesByMentorId(id);
    }

    @Override
    public List<Tag> getTagsByMentor(UUID id) {
        return mentorRepository.findTagsByMentorId(id);
    }

    @Override
    public Optional<CV> getCVById(UUID id) {
        return mentorRepository.findCVByMentorId(id);
    }

    @Override
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

    @Override
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

    @Override
    public Optional<Mentor> findByEmail(String email){
        return mentorRepository.findByEmail(email);
    }
}
