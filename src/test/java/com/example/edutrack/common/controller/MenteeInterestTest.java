package com.example.edutrack.common.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagAPIControllerTest {

    @Mock
    private TagRepository tagRepository;
    @Mock
    private MenteeRepository menteeRepository;

    @InjectMocks
    private TagAPIController controller;

    private UUID userId;
    private Mentee mentee;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        mentee = new Mentee();
        mentee.setId(userId);
        mentee.setInterests(null);
    }

    @Test
    void saveInterests_happyPath_saveTags() {
        // Arrange
        TagAPIController.InterestsRequest req = new TagAPIController.InterestsRequest();
        req.setTags(List.of(1, 2));
        req.setSkipped(false);

        Tag tag1 = new Tag(); tag1.setId(1); tag1.setTitle("Java");
        Tag tag2 = new Tag(); tag2.setId(2); tag2.setTitle("Python");

        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));
        when(tagRepository.findAllById(List.of(1, 2))).thenReturn(List.of(tag1, tag2));

        // Act
        ResponseEntity<?> resp = controller.saveInterests(userId, req);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mentee.getInterests()).isEqualTo("Java,Python");
        verify(menteeRepository).save(mentee);
    }

    @Test
    void saveInterests_skip_setsInterestsToSingleSpace() {
        // Arrange
        TagAPIController.InterestsRequest req = new TagAPIController.InterestsRequest();
        req.setSkipped(true);
        req.setTags(Collections.emptyList());
        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));

        // Act
        ResponseEntity<?> resp = controller.saveInterests(userId, req);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mentee.getInterests()).isEqualTo(" ");
        verify(menteeRepository).save(mentee);
    }

    @Test
    void saveInterests_alreadySetInterests_returnsConflict() {
        // Arrange
        mentee.setInterests("Java");
        TagAPIController.InterestsRequest req = new TagAPIController.InterestsRequest();
        req.setSkipped(false);
        req.setTags(List.of(1,2));
        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));

        // Act
        ResponseEntity<?> resp = controller.saveInterests(userId, req);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat((String) resp.getBody()).contains("already selected interests");
        verify(menteeRepository, never()).save(any());
    }

    @Test
    void saveInterests_menteeNotFound_returns404() {
        // Arrange
        TagAPIController.InterestsRequest req = new TagAPIController.InterestsRequest();
        req.setSkipped(false);
        req.setTags(List.of(1,2));
        when(menteeRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> controller.saveInterests(userId, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void saveInterests_noTagsSelected_returnsBadRequest() {
        // Arrange
        TagAPIController.InterestsRequest req = new TagAPIController.InterestsRequest();
        req.setSkipped(false);
        req.setTags(Collections.emptyList());
        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));

        // Act
        ResponseEntity<?> resp = controller.saveInterests(userId, req);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat((String) resp.getBody()).contains("No tags selected");
        verify(menteeRepository, never()).save(any());
    }

    @Test
    void saveInterests_someTagIdsNotExist_returnsBadRequest() {
        // Arrange
        TagAPIController.InterestsRequest req = new TagAPIController.InterestsRequest();
        req.setSkipped(false);
        req.setTags(List.of(1,2,3));
        Tag tag1 = new Tag(); tag1.setId(1); tag1.setTitle("Java");
        Tag tag2 = new Tag(); tag2.setId(2); tag2.setTitle("Python");
        // Only two tags returned, but requested three IDs

        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));
        when(tagRepository.findAllById(List.of(1,2,3))).thenReturn(List.of(tag1, tag2));

        // Act
        ResponseEntity<?> resp = controller.saveInterests(userId, req);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat((String) resp.getBody()).contains("not found");
        verify(menteeRepository, never()).save(any());
    }
}
