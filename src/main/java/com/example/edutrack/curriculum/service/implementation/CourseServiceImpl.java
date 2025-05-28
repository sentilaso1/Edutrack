package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.repository.ApplicantsRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.repository.TeachingMaterialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository,
                             TeachingMaterialsRepository teachingMaterialsRepository,
                             TagRepository tagRepository,
                             CourseTagServiceImpl courseTagService, TeachingMaterialsImpl teachingMaterialsImpl, TagServiceImpl tagServiceImpl, CourseTagServiceImpl courseTagServiceImpl, CourseMentorServiceImpl courseMentorServiceImpl, ApplicantsRepository applicantsRepository) {
        this.courseRepository = courseRepository;
        this.teachingMaterialsRepository = teachingMaterialsRepository;
        this.tagRepository = tagRepository;
        this.courseTagService = courseTagService;
        this.teachingMaterialsImpl = teachingMaterialsImpl;
        this.tagServiceImpl = tagServiceImpl;
        this.courseTagServiceImpl = courseTagServiceImpl;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
        this.applicantsRepository = applicantsRepository;
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return null;
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
        course.setMentor(null);

        courseRepository.save(course);

        // Tạo tag và CourseTag qua service tag
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

        if (courseFormDTO.getFiles() != null) {
            for (MultipartFile file : courseFormDTO.getFiles()) {
                if (!file.isEmpty()) {
                    try {
                        TeachingMaterial teachingMaterial = new TeachingMaterial();
                        teachingMaterial.setCourse(course);
                        teachingMaterial.setFile(file.getBytes());
                        teachingMaterial.setName(file.getOriginalFilename());
                        teachingMaterial.setFileType(file.getContentType());

                        teachingMaterialsRepository.save(teachingMaterial);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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



        if (courseFormDTO.getFiles() != null) {
            for (MultipartFile file : courseFormDTO.getFiles()) {
                if (!file.isEmpty()) {
                    try {
                        TeachingMaterial teachingMaterial = new TeachingMaterial();
                        teachingMaterial.setCourse(course);
                        teachingMaterial.setName(file.getOriginalFilename());
                        teachingMaterial.setFile(file.getBytes());

                        teachingMaterialsRepository.save(teachingMaterial);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Lỗi khi lưu file " + file.getOriginalFilename(), e);
                    }
                } else {
                    System.out.println("[DEBUG] File rỗng: " + file.getOriginalFilename());
                }
            }
        } else {
            System.out.println("[DEBUG] Không có file nào được upload.");
        }
    }

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }


    @Override
    public List<Course> getFilteredCourses(String search,
                                           String mentorSearch,
                                           Boolean open,
                                           Date fromDate,
                                           Date toDate,
                                           String sortBy) {
        try {
            if ((search == null || search.trim().isEmpty()) &&
                    (mentorSearch == null || mentorSearch.trim().isEmpty()) &&
                    open == null &&
                    fromDate == null &&
                    toDate == null &&
                    (sortBy == null || sortBy.trim().isEmpty())) {

                return courseRepository.findAllOrderByCreatedDate();
            }

            String trimmedSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
            String trimmedMentorSearch = (mentorSearch == null || mentorSearch.trim().isEmpty()) ? null : mentorSearch.trim();
            if ("name".equalsIgnoreCase(sortBy)) {
                return courseRepository.findFilteredCoursesOrderByName(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate);
            } else if ("createdDate".equalsIgnoreCase(sortBy)) {
                return courseRepository.findFilteredCoursesOrderByCreatedDate(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate);
            } else if ("mentorName".equalsIgnoreCase(sortBy)) {
                return courseRepository.findFilteredCoursesOrderByMentorName(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate);
            } else {
                return courseRepository.findFilteredCoursesDefault(trimmedSearch, trimmedMentorSearch, open, fromDate, toDate);
            }
        } catch (Exception e) {
            System.err.println("Error in getFilteredCourses: " + e.getMessage());
            e.printStackTrace();
            return courseRepository.findAllOrderByCreatedDate();
        }
    }

    @Override
    public void delete(UUID id) {
        courseRepository.deleteById(id);
    }

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

}
