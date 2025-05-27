package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkService {
    Page<Bookmark> findAllBookmarks(Pageable pageable);
    Page<Bookmark> findAllBookmarks(Pageable pageable, User user);
    Page<BookmarkDTO> findAllBookmarkWithCourseTags(Pageable pageable);
    Page<BookmarkDTO> findAllBookmarkWithCourseTags(Pageable pageable, User user);
    Bookmark save(Bookmark bookmark);
    void delete(Bookmark bookmark);
}
