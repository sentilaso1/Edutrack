package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.dto.BookmarkDTO;
import com.example.edutrack.accounts.dto.BookmarkFilterForm;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.curriculum.model.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String bookmarkList(
            Model model, @PathVariable int page,
            @ModelAttribute BookmarkFilterForm params,
            HttpSession session
    ) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }

        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            return "redirect:/login";
        }

        List<Integer> selectedTags = params.getTags();
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
        Page<BookmarkDTO> bookmarkPage = bookmarkService.queryAll(params, pageable, user);
        List<BookmarkDTO> bookmarks;

        // TODO: Optimize this logic to avoid redundant queries
        if (params.getSort() == null || params.getSort().equals(BookmarkFilterForm.SORT_DATE_DESC)) {
            bookmarks = bookmarkService.findAllBookmarkWithCourseTagsDateDesc(Pageable.unpaged(), user).getContent();
        } else {
            bookmarks = bookmarkService.findAllBookmarkWithCourseTagsDateAsc(Pageable.unpaged(), user).getContent();
        }
        List<Tag> tags = bookmarkService.findAllUniqueTags(bookmarks);

        model.addAttribute("pageNumber", page);
        model.addAttribute("page", bookmarkPage);
        model.addAttribute("tags", tags);
        model.addAttribute("selectedTags", selectedTags);

        return "/bookmarks/list-bookmark";
    }

    @GetMapping("/bookmark/list")
    public String bookmarkRedirect() {
        return "redirect:/bookmark/list/1";
    }

    @PostMapping("/bookmark/delete/{id}")
    public String deleteBookmark(@PathVariable Long id) {
        bookmarkService.delete(id);
        return "redirect:/bookmark/list/1";
    }
}
