package com.snapmark.controller;

import com.snapmark.model.Bookmark;
import com.snapmark.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public List<Bookmark> list(@RequestParam(required = false) String tag,
                               @RequestParam(required = false) String keyword) {
        if (tag != null) {
            return bookmarkService.findByTag(tag);
        }
        if (keyword != null) {
            return bookmarkService.search(keyword);
        }
        return bookmarkService.findAll();
    }

    @GetMapping("/{id}")
    public Bookmark get(@PathVariable Long id) {
        return bookmarkService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Bookmark> create(@RequestBody Bookmark bookmark) {
        Bookmark created = bookmarkService.create(bookmark);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Bookmark update(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        return bookmarkService.update(id, bookmark);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookmarkService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
