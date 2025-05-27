package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<Bookmark> findAllByUserOrderByCreatedDateDesc(Pageable pageable, User user);
}
