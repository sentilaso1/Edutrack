package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.dto.TagEnrollmentCountDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.SuggestionType;

import java.util.List;
import java.util.UUID;

public interface SuggestionService {
    List<CourseMentor> getSuggestedCourses(SuggestionType type, UUID menteeId, int limit);

    List<Mentor> getSuggestedMentors(SuggestionType type, UUID menteeId, int limit);

    List<TagEnrollmentCountDTO> getSuggestedTags(SuggestionType type, int limit);
}
