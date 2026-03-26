package com.snapmark.repository;

import com.snapmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT b FROM Bookmark b WHERE b.tags LIKE %:tag%")
    List<Bookmark> findByTag(@Param("tag") String tag);

    @Query("SELECT b FROM Bookmark b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Bookmark> searchByKeyword(@Param("keyword") String keyword);
}
