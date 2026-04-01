package com.snapmark.service;

import com.snapmark.model.Snippet;
import com.snapmark.repository.SnippetRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnippetService {

    private final SnippetRepository snippetRepository;

    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    public List<Snippet> findAll() {
        return snippetRepository.findAll();
    }

    public Snippet findById(Long id) {
        return snippetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Snippet not found: " + id));
    }

    public Snippet create(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    public Snippet update(Long id, Snippet updated) {
        Snippet existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setCode(updated.getCode());
        existing.setLanguage(updated.getLanguage());
        existing.setTags(updated.getTags());
        existing.setDescription(updated.getDescription());
        return snippetRepository.save(existing);
    }

    public void delete(Long id) {
        snippetRepository.deleteById(id);
    }

    public List<Snippet> findByTag(String tag) {
        return snippetRepository.findByTag(tag);
    }

    public List<Snippet> findByLanguage(String language) {
        return snippetRepository.findByLanguage(language);
    }

    public List<Snippet> search(String keyword) {
        return snippetRepository.searchByKeyword(keyword);
    }

    /**
     * Find snippets by multiple tags with configurable AND/OR logic.
     * Implemented in service layer using Stream API since tags are stored as comma-separated strings.
     * 
     * @param tags comma-separated string of tags (e.g., "java,spring")
     * @param useAndLogic if true, returns snippets matching ALL tags; if false, returns snippets matching ANY tag
     * @return list of matching snippets
     */
    public List<Snippet> findByMultipleTags(String tags, boolean useAndLogic) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
        
        if (tagList.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Get all snippets and filter in memory
        List<Snippet> allSnippets = snippetRepository.findAll();
        
        if (useAndLogic) {
            // AND logic: snippet must contain ALL specified tags
            return allSnippets.stream()
                    .filter(snippet -> {
                        if (snippet.getTags() == null || snippet.getTags().trim().isEmpty()) {
                            return false;
                        }
                        List<String> snippetTags = Arrays.stream(snippet.getTags().split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        // Check if snippet contains all requested tags
                        return tagList.stream().allMatch(tag -> 
                            snippetTags.stream().anyMatch(sTag -> matchesTag(sTag, tag))
                        );
                    })
                    .collect(Collectors.toList());
        } else {
            // OR logic (default): snippet must contain ANY of the specified tags
            return allSnippets.stream()
                    .filter(snippet -> {
                        if (snippet.getTags() == null || snippet.getTags().trim().isEmpty()) {
                            return false;
                        }
                        List<String> snippetTags = Arrays.stream(snippet.getTags().split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        // Check if snippet contains any of the requested tags
                        return tagList.stream().anyMatch(tag -> 
                            snippetTags.stream().anyMatch(sTag -> matchesTag(sTag, tag))
                        );
                    })
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Helper method to match tags precisely.
     * Ensures exact tag matching to avoid partial matches (e.g., "java" should not match "javascript").
     */
    private boolean matchesTag(String snippetTag, String targetTag) {
        return snippetTag.equalsIgnoreCase(targetTag);
    }

    /**
     * Get all tags from all snippets as a parsed list.
     * Handles null/empty tags safely to prevent NullPointerException.
     */
    public List<String> getAllTags() {
        List<Snippet> snippets = snippetRepository.findAll();
        return snippets.stream()
                .map(Snippet::getTags)
                .filter(tags -> tags != null && !tags.trim().isEmpty())
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
