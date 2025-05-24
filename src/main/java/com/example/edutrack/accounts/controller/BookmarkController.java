package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/bookmark/list")
    public String bookmarkList(Model model) {
        return "/bookmarks/list-bookmark";
    }
}
