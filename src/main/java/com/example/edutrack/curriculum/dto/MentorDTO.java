package com.example.edutrack.curriculum.dto;

import java.util.Base64;
import java.util.UUID;

public class MentorDTO {
    private UUID id;
    private String name;
    private String avatarBase64;

    public MentorDTO(UUID id, String name, byte[] avatar) {
        this.id = id;
        this.name = name;
        if (avatar != null) {
            this.avatarBase64 = Base64.getEncoder().encodeToString(avatar);
        }
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAvatarBase64() {
        return avatarBase64;
    }

    @Override
    public String toString() {
        return "MentorDTO {id=" + id + ", name=" + name + ", avatarBase64=" + avatarBase64 + "}";
    }
}
