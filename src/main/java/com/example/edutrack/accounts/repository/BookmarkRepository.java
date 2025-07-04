package com.example.edutrack.accounts.repository;

import com.example.edutrack.accounts.model.Bookmark;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByOrderByCreatedDateDesc();
    List<Bookmark> findAllByOrderByCreatedDateAsc();

    Page<Bookmark> findAllByOrderByCreatedDateAsc(Pageable pageable);
    Page<Bookmark> findAllByUserOrderByCreatedDateAsc(Pageable pageable, User user);
    Page<Bookmark> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<Bookmark> findAllByUserOrderByCreatedDateDesc(Pageable pageable, User user);

    @Query("""
            SELECT b FROM Bookmark b
            JOIN Course c ON b.course.id = c.id
            JOIN CourseTag ct ON c.id = ct.course.id
            JOIN Tag t ON ct.tag.id = t.id
            WHERE t.id in (:tagId)
            ORDER BY b.createdDate DESC
            """)
    Page<Bookmark> findAllContainingTagCreatedDateDesc(@Param("tagId") List<Integer> tagId, Pageable pageable);

    @Query("""
            SELECT b FROM Bookmark b
            JOIN Course c ON b.course.id = c.id
            JOIN CourseTag ct ON c.id = ct.course.id
            JOIN Tag t ON ct.tag.id = t.id
            WHERE t.id in (:tagId)
            ORDER BY b.createdDate ASC
            """)
    Page<Bookmark> findAllContainingTagCreatedDateAsc(@Param("tagId") List<Integer> tagId, Pageable pageable);

    @Query("""
            SELECT b FROM Bookmark b
            JOIN User u ON b.user.id = u.id
            JOIN Course c ON b.course.id = c.id
            JOIN CourseTag ct ON c.id = ct.course.id
            JOIN Tag t ON ct.tag.id = t.id
            WHERE t.id in (:tagId) AND u.id = :userId
            ORDER BY b.createdDate DESC
            """)
    Page<Bookmark> findAllByUserContainingTagCreatedDateDesc(@Param("tagId") List<Integer> tagId, @Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT b FROM Bookmark b
            JOIN User u ON b.user.id = u.id
            JOIN Course c ON b.course.id = c.id
            JOIN CourseTag ct ON c.id = ct.course.id
            JOIN Tag t ON ct.tag.id = t.id
            WHERE t.id in (:tagId) AND u.id = :userId
            ORDER BY b.createdDate ASC
            """)
    Page<Bookmark> findAllByUserContainingTagCreatedDateAsc(@Param("tagId") List<Integer> tagId, @Param("userId") UUID userId, Pageable pageable);

    boolean existsByCourseAndUser(Course course, User user);
}
