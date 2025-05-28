package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.model.TeachingMaterial;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.repository.TeachingMaterialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository,
                             TeachingMaterialsRepository teachingMaterialsRepository,
                             TagRepository tagRepository,
                             CourseTagServiceImpl courseTagService) {
        this.courseRepository = courseRepository;
        this.teachingMaterialsRepository = teachingMaterialsRepository;
        this.tagRepository = tagRepository;
        this.courseTagService = courseTagService;
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
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
                    }
                }
            }
        }
    }

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getFilteredCourses(String search,
                                           UUID mentorId,
                                           Boolean open,
                                           Date fromDate,
                                           Date toDate,
                                           String sortBy) {
        return courseRepository.findFilteredCourses(
                (search == null || search.isEmpty()) ? null : search,
                mentorId,
                open,
                fromDate,
                toDate,
                (sortBy == null || sortBy.isEmpty()) ? null : sortBy);
    }
}
