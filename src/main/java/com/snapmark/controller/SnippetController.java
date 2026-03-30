package com.snapmark.controller;

import com.snapmark.model.Snippet;
import com.snapmark.service.SnippetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @GetMapping
    public List<Snippet> list(@RequestParam(required = false) String tag,
                              @RequestParam(required = false) String tags,
                              @RequestParam(required = false) String language,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "or") String logic) {
        if (tags != null && !tags.isBlank()) {
            return snippetService.findByTags(tags, logic);
        }
        if (tag != null) {
            return snippetService.findByTag(tag);
        }
        if (language != null) {
            return snippetService.findByLanguage(language);
        }
        if (keyword != null) {
            return snippetService.search(keyword);
        }
        return snippetService.findAll();
    }

    @GetMapping("/{id}")
    public Snippet get(@PathVariable Long id) {
        return snippetService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Snippet> create(@RequestBody Snippet snippet) {
        Snippet created = snippetService.create(snippet);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Snippet update(@PathVariable Long id, @RequestBody Snippet snippet) {
        return snippetService.update(id, snippet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        snippetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tags")
    public List<String> getAllTags() {
        return snippetService.getAllTags();
    }
}
