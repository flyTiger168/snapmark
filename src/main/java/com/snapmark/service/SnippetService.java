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
