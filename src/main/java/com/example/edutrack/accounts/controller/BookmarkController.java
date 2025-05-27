package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookmarkController {
    public static final int PAGE_SIZE = 15;

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/bookmark/list/{page}")
    public String bookmarkList(Model model, @PathVariable int page) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }

        model.addAttribute("pageNumber", page);
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        Page<BookmarkDTO> bookmarkPage = bookmarkService.findAllBookmarkWithCourseTags(pageable);
        model.addAttribute("page", bookmarkPage);

        return "/bookmarks/list-bookmark";
    }

    @GetMapping("/bookmark/list")
    public String bookmarkRedirect() {
        return "redirect:/bookmark/list/1";
    }
}
