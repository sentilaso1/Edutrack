package com.example.edutrack.accounts.service.implementations;

import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.BookmarkRepository;
import com.example.edutrack.accounts.service.interfaces.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Override
    public List<Bookmark> findAllBookmarks(User user) {
        return bookmarkRepository.findAllByUserOrderByCreatedDateDesc(user);
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
