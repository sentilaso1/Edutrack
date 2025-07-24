package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.*;
import com.example.edutrack.curriculum.repository.*;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.profiles.model.CV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TeachingMaterialsRepository teachingMaterialsRepository;
    private final TagRepository tagRepository;
    private final CourseTagServiceImpl courseTagService;
    private final TeachingMaterialsImpl teachingMaterialsImpl;
    private final TagServiceImpl tagServiceImpl;
    private final CourseTagServiceImpl courseTagServiceImpl;
    private final CourseMentorServiceImpl courseMentorServiceImpl;
    private final ApplicantsRepository applicantsRepository;
    private final MentorRepository mentorRepository;
    private final CVCourseRepository cvCourseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository,
                             TeachingMaterialsRepository teachingMaterialsRepository,
                             TagRepository tagRepository,
                             CourseTagServiceImpl courseTagService, TeachingMaterialsImpl teachingMaterialsImpl, TagServiceImpl tagServiceImpl, CourseTagServiceImpl courseTagServiceImpl, CourseMentorServiceImpl courseMentorServiceImpl, ApplicantsRepository applicantsRepository,
                             MentorRepository mentorRepository,
                             CVCourseRepository cvCourseRepository) {
        this.courseRepository = courseRepository;
        this.teachingMaterialsRepository = teachingMaterialsRepository;
        this.tagRepository = tagRepository;
        this.courseTagService = courseTagService;
        this.teachingMaterialsImpl = teachingMaterialsImpl;
        this.tagServiceImpl = tagServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
        this.applicantsRepository = applicantsRepository;
        this.mentorRepository = mentorRepository;
        this.cvCourseRepository = cvCourseRepository;
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public void save(Course course){
        courseRepository.save(course);
    }

    @Override
    public Course findById(UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course Not Found"));
    }

    @Override
    public UUID create(CourseFormDTO courseFormDTO) {
        Course course = new Course();
        course.setName(courseFormDTO.getName());
        course.setDescription(courseFormDTO.getDescription());
        course.setOpen(false);

        courseRepository.save(course);

        if (courseFormDTO.getTagTexts() != null) {
            for (String title : courseFormDTO.getTagTexts()) {
                Tag tag = tagRepository.findByTitle(title)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTitle(title);
                            return tagRepository.save(newTag);
                        });
                courseTagService.addCourseTag(course.getId(), tag.getId());
            }
        }
        return course.getId();
    }

    @Override
    public void update(UUID id, CourseFormDTO courseFormDTO) {
        Course course = findById(id);
        course.setName(courseFormDTO.getName());
        course.setDescription(courseFormDTO.getDescription());
        courseRepository.save(course);

        if (courseFormDTO.getTagTexts() != null) {
            for (String title : courseFormDTO.getTagTexts()) {
                Tag tag = tagRepository.findByTitle(title)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTitle(title);
                            return tagRepository.save(newTag);
                        });

                boolean exists = courseTagService.existsByCourseAndTag(course.getId(), tag.getId());
                if (!exists) {
                    courseTagService.addCourseTag(course.getId(), tag.getId());
                }
            }
        }
    }

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public Page<Course> getAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public Page<Course> findFilteredCourses(List<UUID> skillIds, List<Integer> subjectIds, Pageable pageable) {
        return courseRepository.findFilteredCourses(skillIds, subjectIds, pageable);
    }

    @Override
    public Page<Course> getFilteredCourses(String search,
                                           String mentorSearch,
                                           Boolean open,
                                           Date fromDate,
                                           Date toDate,
                                           String sortBy,
                                           Pageable pageable) {
        try {
            String trimmedSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
            String trimmedMentorSearch = (mentorSearch == null || mentorSearch.trim().isEmpty()) ? null : mentorSearch.trim();

            if ((trimmedSearch == null) &&
                    (trimmedMentorSearch == null) &&
                    open == null &&
                    fromDate == null &&
                    toDate == null &&
                    (sortBy == null || sortBy.trim().isEmpty())) {
                return courseRepository.findAllOrderByCreatedDate(pageable);
            }

            if ("name".equalsIgnoreCase(sortBy)) {
                return courseRepository.findFilteredCoursesOrderByName(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate, pageable);
            } else if ("createdDate".equalsIgnoreCase(sortBy)) {
                return courseRepository.findFilteredCoursesOrderByCreatedDate(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate, pageable);
            } else if ("mentorName".equalsIgnoreCase(sortBy)) {
                return courseRepository.findFilteredCoursesOrderByMentorName(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate, pageable);
            } else {
                return courseRepository.findFilteredCoursesDefault(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate, pageable);
            }
        } catch (Exception e) {
            System.err.println("Error in getFilteredCourses: " + e.getMessage());
            e.printStackTrace();
            return courseRepository.findAllOrderByCreatedDate(pageable);
        }
    }

    @Override
    public Map<UUID, List<Mentor>> getAcceptedMentorsForCourses(List<Course> courses){
        Map<UUID, List<Mentor>> courseMentorMap = new HashMap<>();
        List<String> approvedStatuses = List.of(CV.STATUS_APPROVED, CV.STATUS_AIAPPROVED);
        for (Course course : courses) {
            List<Mentor> acceptedMentors = mentorRepository.findMentorsByCourseAndCVStatusIn(course, approvedStatuses);
            courseMentorMap.put(course.getId(), acceptedMentors);
        }
        return courseMentorMap;
    }

    @Override
    public Map<UUID, Integer> getPendingApplicantCountForCourses(List<Course> courses) {
        Map<UUID, Integer> pendingCountMap = new HashMap<>();
        for (Course course : courses) {
            int count = cvCourseRepository.countByCourseAndCVStatus(course, CV.STATUS_PENDING);
            pendingCountMap.put(course.getId(), count);
        }
        return pendingCountMap;
    }

    @Override
    public void delete(UUID id) {
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteCourseWithRelatedData(UUID courseId) {

        List<TeachingMaterial> courseMaterials = teachingMaterialsImpl.findByCourseId(courseId);
        for (TeachingMaterial teachingMaterial : courseMaterials) {
            teachingMaterialsImpl.deleteById(teachingMaterial.getId());
        }

        List<Tag> courseTags = tagServiceImpl.findTagsByCourseId(courseId);
        for (Tag tag : courseTags) {
            courseTagServiceImpl.deleteCourseTag(courseId, tag.getId());
        }
        List<CourseMentor> applicants = courseMentorServiceImpl.findByCourseId(courseId);
        for (CourseMentor courseMentor : applicants) {
            applicantsRepository.deleteById(courseMentor.getId());
        }
        this.delete(courseId);
    }

    @Override
    public Page<Course> findAllExcludingIds(List<UUID> excludeIds, Pageable pageable) {
        if (excludeIds == null || excludeIds.isEmpty()) {
            return courseRepository.findAll(pageable);
        }
        return courseRepository.findByIdNotIn(excludeIds, pageable);
    }
}
