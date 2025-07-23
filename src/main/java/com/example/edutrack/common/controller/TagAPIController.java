package com.example.edutrack.common.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.TagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class TagAPIController {

    private final TagRepository tagRepository;
    private final MenteeRepository menteeRepository;

    public TagAPIController(TagRepository tagRepository, MenteeRepository menteeRepository) {
        this.tagRepository = tagRepository;
        this.menteeRepository = menteeRepository;
    }

    @GetMapping("/api/tags")
    public List<Tag> getTags(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) return tagRepository.findAll();
        return tagRepository.findByTitleContainingIgnoreCase(search);
    }

    @PostMapping("/api/mentees/{userId}/interests")
    public ResponseEntity<?> saveInterests(
            @PathVariable UUID userId,
            @RequestBody InterestsRequest request
    ) {
        Mentee mentee = menteeRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mentee not found"));
        if (mentee.getInterests() != null && !mentee.getInterests().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User already selected interests or skipped.");
        }

        if (request.isSkipped()) {
            mentee.setInterests(" ");
        } else {
            List<Integer> tagIds = request.getTags();
            if (tagIds == null || tagIds.isEmpty()) {
                return ResponseEntity.badRequest().body("No tags selected.");
            }

            List<Tag> tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                return ResponseEntity.badRequest().body("One or more tags not found.");
            }

            List<String> tagsTitle = tags.stream()
                    .map(Tag::getTitle)
                    .toList();

            String interestStr = tagsTitle.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            mentee.setInterests(interestStr);
        }
        menteeRepository.save(mentee);
        return ResponseEntity.ok().build();
    }

    public static class InterestsRequest {
        private List<Integer> tags;
        private boolean skipped;
        public InterestsRequest() {}

        public List<Integer> getTags() {
            return tags;
        }

        public void setTags(List<Integer> tags) {
            this.tags = tags;
        }

        public boolean isSkipped() {
            return skipped;
        }

        public void setSkipped(boolean skipped) {
            this.skipped = skipped;
        }
    }
}
