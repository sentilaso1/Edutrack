package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByUserOrderByCreatedDateDesc(User user);
}
