package com.snapmark.service;

import com.snapmark.model.Snippet;
import com.snapmark.repository.SnippetRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    /**
     * Find snippets by multiple tags with configurable AND/OR logic.
     * @param tags list of tags to filter by
     * @param logic "and" or "or" - determines whether all tags must match or any tag matches
     * @return list of matching snippets
     */
    public List<Snippet> findByTags(List<String> tags, String logic) {
        if (tags == null || tags.isEmpty()) {
            return findAll();
        }
        
        // Get all snippets and filter in memory for flexibility
        List<Snippet> allSnippets = findAll();
        
        return allSnippets.stream()
            .filter(snippet -> {
                String snippetTags = snippet.getTags();
                if (snippetTags == null || snippetTags.trim().isEmpty()) {
                    return false;
                }
                
                List<String> snippetTagList = Arrays.stream(snippetTags.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
                
                if ("and".equalsIgnoreCase(logic)) {
                    // AND logic: snippet must have ALL specified tags
                    return tags.stream().allMatch(tag -> 
                        snippetTagList.stream().anyMatch(st -> st.contains(tag))
                    );
                } else {
                    // OR logic (default): snippet must have AT LEAST ONE of the specified tags
                    return tags.stream().anyMatch(tag -> 
                        snippetTagList.stream().anyMatch(st -> st.contains(tag))
                    );
                }
            })
            .collect(Collectors.toList());
    }

    public List<Snippet> findByLanguage(String language) {
        return snippetRepository.findByLanguage(language);
    }

    public List<Snippet> search(String keyword) {
        return snippetRepository.searchByKeyword(keyword);
    }

    /**
     * Get all tags from all snippets as a parsed list.
     * BUG: does not handle null/empty tags — will throw NullPointerException
     */
    public List<String> getAllTags() {
        List<Snippet> snippets = snippetRepository.findAll();
        return snippets.stream()
                .map(Snippet::getTags)
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
