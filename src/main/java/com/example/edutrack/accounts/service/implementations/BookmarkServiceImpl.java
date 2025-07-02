package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.dto.BookmarkFilterForm;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.BookmarkRepository;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.service.implementation.TagServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final TagService tagService;

    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository, TagService tagService) {
        this.bookmarkRepository = bookmarkRepository;
        this.tagService = tagService;
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
    public Page<BookmarkDTO> queryAll(BookmarkFilterForm params, Pageable pageable) {
        String sort = params.getSort();
        List<Integer> tagIds = params.getTags();

        if (sort == null || sort.equals(BookmarkFilterForm.SORT_DATE_DESC)) {
            if (tagIds == null || tagIds.isEmpty()) {
                return this.findAllBookmarkWithCourseTagsDateDesc(pageable);
            }
            return this.findAllBookmarkContainingTagsDateDesc(pageable, tagIds);
        }

        if (tagIds == null || tagIds.isEmpty()) {
            return this.findAllBookmarkWithCourseTagsDateAsc(pageable);
        }
        return this.findAllBookmarkContainingTagsDateAsc(pageable, tagIds);
    }

    @Override
    public Page<BookmarkDTO> queryAll(BookmarkFilterForm params, Pageable pageable, User user) {
        String sort = params.getSort();
        List<Integer> tagIds = params.getTags();

        if (sort == null || sort.equals(BookmarkFilterForm.SORT_DATE_DESC)) {
            if (tagIds == null || tagIds.isEmpty()) {
                return this.findAllBookmarkWithCourseTagsDateDesc(pageable, user);
            }
            return this.findAllBookmarkContainingTagsDateDesc(pageable, user, tagIds);
        }

        if (tagIds == null || tagIds.isEmpty()) {
            return this.findAllBookmarkWithCourseTagsDateAsc(pageable, user);
        }
        return this.findAllBookmarkContainingTagsDateAsc(pageable, user, tagIds);
    }

    @Override
    public List<Tag> findAllUniqueTags(List<BookmarkDTO> bookmarkDTOs) {
        return bookmarkDTOs.stream()
                .flatMap(bookmarkDTO -> bookmarkDTO.getTags().stream())
                .distinct()
                .toList();
    }

    @Override
    public List<Tag> findAllSelectedTags(List<Integer> tagIds) {
        List<Optional<Tag>> optionals = tagService.findById(tagIds);
        List<Tag> selectedTags = new ArrayList<>();

        for (Optional<Tag> optional : optionals) {
            if (optional.isEmpty()) {
                continue;
            }
            selectedTags.add(optional.get());
        }
        return selectedTags;
    }

    private Page<BookmarkDTO> getBookmarkDTOS(Page<Bookmark> bookmarkPage) {
        return bookmarkPage.map(bookmark -> {
            BookmarkDTO bookmarkDTO = new BookmarkDTO();
            bookmarkDTO.setBookmark(bookmark);

            List<Tag> tags = tagService.findTagsByCourseId(bookmark.getCourse().getId());
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
        this.delete(bookmark.getId());
    }

    @Override
    public void delete(Long id) {
        bookmarkRepository.deleteById(id);
    }

    @Override
    public Optional<Bookmark> findById(Long id) {
        return bookmarkRepository.findById(id);
    }
}
