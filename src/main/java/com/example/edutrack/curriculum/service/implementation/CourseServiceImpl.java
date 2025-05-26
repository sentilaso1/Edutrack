package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.CourseFormDTO;
import com.example.edutrack.curriculum.model.*;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.repository.CourseTagsRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.repository.TeachingMaterialsRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final CourseTagsRepository courseTagsRepository;
    private final TagRepository tagRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, TeachingMaterialsRepository teachingMaterialsRepository, CourseTagsRepository courseTagsRepository, TagRepository tagRepository) {
        this.courseRepository = courseRepository;
        this.teachingMaterialsRepository = teachingMaterialsRepository;
        this.courseTagsRepository = courseTagsRepository;
        this.tagRepository = tagRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(UUID id) {
        return courseRepository.findById(id).orElseThrow( () -> new RuntimeException("Course Not Found"));
    }



    public UUID create(CourseFormDTO courseFormDTO) {
        Course course = new Course();
        course.setName(courseFormDTO.getName());
        course.setDescription(courseFormDTO.getDescription());
        Mentor mentor = new Mentor();
        mentor.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        course.setMentor(mentor);

        courseRepository.save(course);

        if (courseFormDTO.getTagTexts() != null) {
            for (String title : courseFormDTO.getTagTexts()) {
                Tag tag = tagRepository.findByTitle(title)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTitle(title);
                            return tagRepository.save(newTag);
                        });
                CourseTag ct = new CourseTag(course, tag);
                courseTagsRepository.save(ct);
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

                boolean exists = courseTagsRepository.existsByCourseAndTag(course, tag);
                if (!exists) {
                    courseTagsRepository.save(new CourseTag(course, tag));
                }
            }
        }
        List<TeachingMaterial> oldMaterials = teachingMaterialsRepository.findByCourseId(course.getId());
        teachingMaterialsRepository.deleteAll(oldMaterials);
        if (courseFormDTO.getFiles() != null) {
            for (MultipartFile file : courseFormDTO.getFiles()) {
                if(!file.isEmpty()) {
                    try {
                        TeachingMaterial teachingMaterial = new TeachingMaterial();
                        teachingMaterial.setCourse(course);
                        teachingMaterial.setFile(file.getBytes());
                        teachingMaterialsRepository.save(teachingMaterial);
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public List<Course> getAll(){
        return courseRepository.findAll();
    }

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
