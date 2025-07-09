package com.example.edutrack.timetables.dto;

import java.time.LocalDateTime;

public class ScheduleActivityBannerDTO {
    private String message;
    private LocalDateTime updateDate;

    public ScheduleActivityBannerDTO(String message, LocalDateTime updateDate) {
        this.message = message;
        this.updateDate = updateDate;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
}
