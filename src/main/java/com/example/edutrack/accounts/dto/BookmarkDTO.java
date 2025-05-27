package com.example.edutrack.accounts.dto;

import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;

import java.util.List;

public class BookmarkDTO {
    private Bookmark bookmark;
    private List<Tag> tags;

    public BookmarkDTO() {

    }

    public BookmarkDTO(Bookmark bookmark, List<Tag> tags) {
        this.bookmark = bookmark;
        this.tags = tags;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public void setBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


}
