package com.snapmark.repository;

import com.snapmark.model.Snippet;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long>, JpaSpecificationExecutor<Snippet> {

    List<Snippet> findByLanguage(String language);

    /**
     * Search snippets by a single tag.
     * Uses LIKE query on the comma-separated tags field.
     */
    @Query("SELECT s FROM Snippet s WHERE CONCAT(',', s.tags, ',') LIKE %:tag%")
    List<Snippet> findByTag(@Param("tag") String tag);

    @Query("SELECT s FROM Snippet s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Snippet> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find snippets by multiple tags with AND/OR logic.
     * @param tags list of tags to filter by
     * @param useAnd if true, match ALL tags (AND); if false, match ANY tag (OR)
     * @return list of matching snippets
     */
    default List<Snippet> findByMultipleTags(List<String> tags, boolean useAnd) {
        if (tags == null || tags.isEmpty()) {
            return findAll();
        }

        Specification<Snippet> spec = null;

        if (useAnd) {
            // AND logic: snippet must contain ALL specified tags
            for (String tag : tags) {
                String trimmedTag = tag.trim();
                Specification<Snippet> tagSpec = (root, query, cb) ->
                    cb.like(cb.concat(cb.concat(",", root.get("tags")), ","), "%" + trimmedTag + "%");
                spec = spec == null ? tagSpec : spec.and(tagSpec);
            }
        } else {
            // OR logic: snippet must contain ANY of the specified tags
            for (String tag : tags) {
                String trimmedTag = tag.trim();
                Specification<Snippet> tagSpec = (root, query, cb) ->
                    cb.like(cb.concat(cb.concat(",", root.get("tags")), ","), "%" + trimmedTag + "%");
                spec = spec == null ? tagSpec : spec.or(tagSpec);
            }
        }

        return findAll(spec);
    }
}
