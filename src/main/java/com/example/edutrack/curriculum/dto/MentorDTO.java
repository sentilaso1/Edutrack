package com.example.edutrack.curriculum.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class MentorDTO {
    private UUID id;
    private String name;
    private byte[] avatar;

    public MentorDTO(UUID id, String name, byte[] avatar) {
        this.id = id;
        this.name = name;
        if (avatar != null) {
            this.avatar = avatar;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setAvatar(MultipartFile banner) throws IOException {
        this.avatar = banner.getBytes();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MentorDTO {id=" + id + ", name=" + name + ", avatarBase64=" + avatar + "}";
    }
}
