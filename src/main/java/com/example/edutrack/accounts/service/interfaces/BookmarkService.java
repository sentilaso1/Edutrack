package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkService {
    Page<Bookmark> findAllBookmarksDateDesc();
    Page<Bookmark> findAllBookmarksDateAsc();

    Page<Bookmark> findAllBookmarksDateDesc(Pageable pageable);
    Page<Bookmark> findAllBookmarksDateDesc(Pageable pageable, User user);

    Page<Bookmark> findAllBookmarksDateAsc(Pageable pageable);
    Page<Bookmark> findAllBookmarksDateAsc(Pageable pageable, User user);

    // Retrieves bookmarks and their associated course tags
    Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateDesc(Pageable pageable);
    Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateDesc(Pageable pageable, User user);

    Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateAsc(Pageable pageable);
    Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateAsc(Pageable pageable, User user);

    // Retrieves bookmarks containing specific tags
    Page<BookmarkDTO> findAllBookmarkContainingTagsDateDesc(Pageable pageable, List<Integer> tagIds);
    Page<BookmarkDTO> findAllBookmarkContainingTagsDateDesc(Pageable pageable, User user, List<Integer> tagIds);

    Page<BookmarkDTO> findAllBookmarkContainingTagsDateAsc(Pageable pageable, List<Integer> tagIds);
    Page<BookmarkDTO> findAllBookmarkContainingTagsDateAsc(Pageable pageable, User user, List<Integer> tagIds);

    List<Tag> findAllUniqueTags(List<BookmarkDTO> bookmarkDTOs);
    Bookmark save(Bookmark bookmark);
    void delete(Bookmark bookmark);
}
