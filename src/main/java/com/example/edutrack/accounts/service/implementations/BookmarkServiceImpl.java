package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.BookmarkRepository;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import com.example.edutrack.curriculum.dto.TagDTO;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.implementation.CourseTagServiceImpl;
import com.example.edutrack.curriculum.service.implementation.TagServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.CourseTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final TagServiceImpl tagServiceImpl;

    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository, TagServiceImpl tagServiceImpl) {
        this.bookmarkRepository = bookmarkRepository;
        this.tagServiceImpl = tagServiceImpl;
    }

    @Override
    public Page<Bookmark> findAllBookmarks(Pageable pageable) {
        return bookmarkRepository.findAllByOrderByCreatedDateDesc(pageable);
    }

    @Override
    public Page<Bookmark> findAllBookmarks(Pageable pageable, User user) {
        return bookmarkRepository.findAllByUserOrderByCreatedDateDesc(pageable, user);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkWithCourseTags(Pageable pageable) {
        Page<Bookmark> bookmarkPage = this.findAllBookmarks(pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkWithCourseTags(Pageable pageable, User user) {
        Page<Bookmark> bookmarkPage = this.findAllBookmarks(pageable, user);
        return getBookmarkDTOS(bookmarkPage);
    }

    private Page<BookmarkDTO> getBookmarkDTOS(Page<Bookmark> bookmarkPage) {
        return bookmarkPage.map(bookmark -> {
            BookmarkDTO bookmarkDTO = new BookmarkDTO();
            bookmarkDTO.setBookmark(bookmark);

            List<Tag> tagDtos = tagServiceImpl.findTagsByCourseId(bookmark.getCourse().getId());
            bookmarkDTO.setTags(tagDtos);

            return bookmarkDTO;
        });
    }

    @Override
    public Bookmark save(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    @Override
    public void delete(Bookmark bookmark) {
        bookmarkRepository.deleteById(bookmark.getId());
    }
}
