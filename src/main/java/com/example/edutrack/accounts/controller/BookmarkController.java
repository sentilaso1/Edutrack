package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.dto.BookmarkFilterForm;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import com.example.edutrack.curriculum.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class BookmarkController {
    public static final int PAGE_SIZE = 15;

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/bookmark/list/{page}")
    public String bookmarkList(Model model, @PathVariable int page, @ModelAttribute BookmarkFilterForm params) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }

        model.addAttribute("pageNumber", page);
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Page<BookmarkDTO> bookmarkPage = null;

        // TODO: Optimize this logic to avoid redundant queries
        List<Tag> tags = null;
        List<BookmarkDTO> bookmarks = null;

        if (params.getSort() == null || params.getSort().equals(BookmarkFilterForm.SORT_DATE_DESC)) {
            if (params.getTags() == null || params.getTags().isEmpty()) {
                bookmarkPage = bookmarkService.findAllBookmarkWithCourseTagsDateDesc(pageable);
            } else {
                bookmarkPage = bookmarkService.findAllBookmarkContainingTagsDateDesc(pageable, params.getTags());
            }
            bookmarks = bookmarkService.findAllBookmarkWithCourseTagsDateDesc(Pageable.unpaged()).getContent();
            tags = bookmarkService.findAllUniqueTags(bookmarks);
        } else {
            if (params.getTags() == null || params.getTags().isEmpty()) {
                bookmarkPage = bookmarkService.findAllBookmarkWithCourseTagsDateAsc(pageable);
            } else {
                bookmarkPage = bookmarkService.findAllBookmarkContainingTagsDateAsc(pageable, params.getTags());
            }
            bookmarks = bookmarkService.findAllBookmarkWithCourseTagsDateAsc(Pageable.unpaged()).getContent();
            tags = bookmarkService.findAllUniqueTags(bookmarks);
        }

        model.addAttribute("page", bookmarkPage);
        model.addAttribute("tags", tags);

        return "/bookmarks/list-bookmark";
    }

    @GetMapping("/bookmark/list")
    public String bookmarkRedirect() {
        return "redirect:/bookmark/list/1";
    }
}
