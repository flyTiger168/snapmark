package com.snapmark.repository;

import com.snapmark.model.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {

    List<Snippet> findByLanguage(String language);

    /**
     * Search snippets by a single tag.
     * Uses LIKE query on the comma-separated tags field.
     */
    @Query("SELECT s FROM Snippet s WHERE s.tags LIKE %:tag%")
    List<Snippet> findByTag(@Param("tag") String tag);

    @Query("SELECT s FROM Snippet s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Snippet> searchByKeyword(@Param("keyword") String keyword);

}
