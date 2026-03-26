package com.snapmark.service;

import com.snapmark.model.Bookmark;
import com.snapmark.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public List<Bookmark> findAll() {
        return bookmarkRepository.findAll();
    }

    public Bookmark findById(Long id) {
        return bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found: " + id));
    }

    public Bookmark create(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    public Bookmark update(Long id, Bookmark updated) {
        Bookmark existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setUrl(updated.getUrl());
        existing.setTags(updated.getTags());
        existing.setDescription(updated.getDescription());
        return bookmarkRepository.save(existing);
    }

    public void delete(Long id) {
        bookmarkRepository.deleteById(id);
    }

    public List<Bookmark> findByTag(String tag) {
        return bookmarkRepository.findByTag(tag);
    }

    public List<Bookmark> search(String keyword) {
        return bookmarkRepository.searchByKeyword(keyword);
    }
}
