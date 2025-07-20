package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.common.controller.TagAPIController;
import com.example.edutrack.curriculum.model.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import com.example.edutrack.curriculum.repository.TagRepository;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagAPIControllerTest {

    @InjectMocks
    private TagAPIController controller;

    @Mock
    private MenteeRepository menteeRepository;

    @Mock
    private TagRepository tagRepository;

    @Test
    void F1_shouldSaveTags_whenValid() {
        UUID userId = UUID.randomUUID();
        Mentee mentee = new Mentee();
        List<Integer> tagIds = List.of(1, 2);
        Tag tag1 = new Tag();
        tag1.setId(1);
        tag1.setTitle("Java");

        Tag tag2 = new Tag();
        tag2.setId(2);
        tag2.setTitle("Spring");
        List<Tag> tags = List.of(tag1, tag2);

        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));
        when(tagRepository.findAllById(tagIds)).thenReturn(tags);

        TagAPIController.InterestsRequest request = new TagAPIController.InterestsRequest();
        request.setSkipped(false);
        request.setTags(tagIds);

        ResponseEntity<?> response = controller.saveInterests(userId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java,Spring", mentee.getInterests());
        verify(menteeRepository).save(mentee);
    }

    @Test
    void F2_shouldSaveSpace_whenSkipped() {
        UUID userId = UUID.randomUUID();
        Mentee mentee = new Mentee();

        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));

        TagAPIController.InterestsRequest request = new TagAPIController.InterestsRequest();
        request.setSkipped(true);

        ResponseEntity<?> response = controller.saveInterests(userId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(" ", mentee.getInterests());
        verify(menteeRepository).save(mentee);
    }


    @Test
    void F3_shouldRejectIfAlreadyHasInterests() {
        UUID userId = UUID.randomUUID();
        Mentee mentee = new Mentee();
        mentee.setInterests("Java");

        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));

        TagAPIController.InterestsRequest request = new TagAPIController.InterestsRequest();
        request.setSkipped(false);
        request.setTags(List.of(1, 2));

        ResponseEntity<?> response = controller.saveInterests(userId, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("already"));
        verify(menteeRepository, never()).save(any());
    }


    @Test
    void F4_shouldThrowIfMenteeNotFound() {
        UUID userId = UUID.randomUUID();
        when(menteeRepository.findById(userId)).thenReturn(Optional.empty());

        TagAPIController.InterestsRequest request = new TagAPIController.InterestsRequest();
        request.setTags(List.of(1));
        request.setSkipped(false);

        assertThrows(ResponseStatusException.class, () -> controller.saveInterests(userId, request));
    }


    @Test
    void F5_shouldRejectIfTagsEmptyOrNull() {
        UUID userId = UUID.randomUUID();
        Mentee mentee = new Mentee();
        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));


        TagAPIController.InterestsRequest nullTagsRequest = new TagAPIController.InterestsRequest();
        nullTagsRequest.setSkipped(false);
        nullTagsRequest.setTags(null);

        ResponseEntity<?> response1 = controller.saveInterests(userId, nullTagsRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertTrue(response1.getBody().toString().contains("No tags"));


        TagAPIController.InterestsRequest emptyTagsRequest = new TagAPIController.InterestsRequest();
        emptyTagsRequest.setSkipped(false);
        emptyTagsRequest.setTags(List.of());

        ResponseEntity<?> response2 = controller.saveInterests(userId, emptyTagsRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertTrue(response2.getBody().toString().contains("No tags"));

        verify(menteeRepository, never()).save(any());
    }

    // F6: Tag IDs include missing ones
    @Test
    void F6_shouldRejectIfSomeTagsMissing() {
        UUID userId = UUID.randomUUID();
        Mentee mentee = new Mentee();
        List<Integer> tagIds = List.of(1, 2);
        Tag tag = new Tag();
        tag.setId(1);
        tag.setTitle("Java");
        List<Tag> foundTags = List.of(tag);

        when(menteeRepository.findById(userId)).thenReturn(Optional.of(mentee));
        when(tagRepository.findAllById(tagIds)).thenReturn(foundTags);

        TagAPIController.InterestsRequest request = new TagAPIController.InterestsRequest();
        request.setSkipped(false);
        request.setTags(tagIds);

        ResponseEntity<?> response = controller.saveInterests(userId, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("tags not found"));

        verify(menteeRepository, never()).save(any());
    }
}
