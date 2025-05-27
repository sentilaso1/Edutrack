package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.BookmarkRepository;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.implementation.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public Page<Bookmark> findAllBookmarksDateDesc() {
        return bookmarkRepository.findAllByOrderByCreatedDateDesc(Pageable.unpaged());
    }

    @Override
    public Page<Bookmark> findAllBookmarksDateAsc() {
        return bookmarkRepository.findAllByOrderByCreatedDateAsc(Pageable.unpaged());
    }

    @Override
    public Page<Bookmark> findAllBookmarksDateDesc(Pageable pageable) {
        return bookmarkRepository.findAllByOrderByCreatedDateDesc(pageable);
    }

    @Override
    public Page<Bookmark> findAllBookmarksDateDesc(Pageable pageable, User user) {
        return bookmarkRepository.findAllByUserOrderByCreatedDateDesc(pageable, user);
    }

    @Override
    public Page<Bookmark> findAllBookmarksDateAsc(Pageable pageable) {
        return bookmarkRepository.findAllByOrderByCreatedDateAsc(pageable);
    }

    @Override
    public Page<Bookmark> findAllBookmarksDateAsc(Pageable pageable, User user) {
        return bookmarkRepository.findAllByUserOrderByCreatedDateAsc(pageable, user);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateDesc(Pageable pageable) {
        Page<Bookmark> bookmarkPage = this.findAllBookmarksDateDesc(pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateDesc(Pageable pageable, User user) {
        Page<Bookmark> bookmarkPage = this.findAllBookmarksDateDesc(pageable, user);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateAsc(Pageable pageable) {
        Page<Bookmark> bookmarkPage = this.findAllBookmarksDateAsc(pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkWithCourseTagsDateAsc(Pageable pageable, User user) {
        Page<Bookmark> bookmarkPage = this.findAllBookmarksDateAsc(pageable, user);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkContainingTagsDateDesc(Pageable pageable, List<Integer> tagIds) {
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllContainingTagCreatedDateDesc(tagIds, pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkContainingTagsDateDesc(Pageable pageable, User user, List<Integer> tagIds) {
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByUserContainingTagCreatedDateDesc(tagIds, user.getId(), pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkContainingTagsDateAsc(Pageable pageable, List<Integer> tagIds) {
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllContainingTagCreatedDateAsc(tagIds, pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public Page<BookmarkDTO> findAllBookmarkContainingTagsDateAsc(Pageable pageable, User user, List<Integer> tagIds) {
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByUserContainingTagCreatedDateAsc(tagIds, user.getId(), pageable);
        return getBookmarkDTOS(bookmarkPage);
    }

    @Override
    public List<Tag> findAllUniqueTags(List<BookmarkDTO> bookmarkDTOs) {
        return bookmarkDTOs.stream()
                .flatMap(bookmarkDTO -> bookmarkDTO.getTags().stream())
                .distinct()
                .toList();
    }

    private Page<BookmarkDTO> getBookmarkDTOS(Page<Bookmark> bookmarkPage) {
        return bookmarkPage.map(bookmark -> {
            BookmarkDTO bookmarkDTO = new BookmarkDTO();
            bookmarkDTO.setBookmark(bookmark);

            List<Tag> tags = tagServiceImpl.findTagsByCourseId(bookmark.getCourse().getId());
            bookmarkDTO.setTags(tags);

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
