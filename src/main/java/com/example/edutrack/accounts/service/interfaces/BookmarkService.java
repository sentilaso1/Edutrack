package com.example.edutrack.accounts.service.interfaces;

import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;

import java.util.List;

public interface BookmarkService {
    List<Bookmark> findAllBookmarks(User user);
    Bookmark save(Bookmark bookmark);
    void delete(Bookmark bookmark);
}
