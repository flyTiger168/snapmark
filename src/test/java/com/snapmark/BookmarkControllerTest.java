package com.snapmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapmark.model.Bookmark;
import com.snapmark.repository.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookmarkRepository.deleteAll();
    }

    @Test
    void shouldCreateBookmark() throws Exception {
        Bookmark bookmark = new Bookmark();
        bookmark.setTitle("Spring Docs");
        bookmark.setUrl("https://spring.io/docs");
        bookmark.setTags("spring,java");

        mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookmark)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Docs"));
    }

    @Test
    void shouldListAllBookmarks() throws Exception {
        Bookmark b1 = new Bookmark();
        b1.setTitle("Bookmark 1");
        b1.setUrl("https://example.com/1");
        b1.setTags("tech");
        bookmarkRepository.save(b1);

        Bookmark b2 = new Bookmark();
        b2.setTitle("Bookmark 2");
        b2.setUrl("https://example.com/2");
        b2.setTags("news");
        bookmarkRepository.save(b2);

        mockMvc.perform(get("/api/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldFilterByTag() throws Exception {
        Bookmark b1 = new Bookmark();
        b1.setTitle("Tech bookmark");
        b1.setUrl("https://example.com");
        b1.setTags("tech,java");
        bookmarkRepository.save(b1);

        Bookmark b2 = new Bookmark();
        b2.setTitle("News bookmark");
        b2.setUrl("https://news.com");
        b2.setTags("news");
        bookmarkRepository.save(b2);

        mockMvc.perform(get("/api/bookmarks").param("tag", "tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Tech bookmark"));
    }
}
