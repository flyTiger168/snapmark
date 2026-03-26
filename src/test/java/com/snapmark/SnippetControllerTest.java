package com.snapmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapmark.model.Snippet;
import com.snapmark.repository.SnippetRepository;
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
class SnippetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        snippetRepository.deleteAll();
    }

    @Test
    void shouldCreateSnippet() throws Exception {
        Snippet snippet = new Snippet();
        snippet.setTitle("Hello World");
        snippet.setCode("System.out.println(\"Hello\");");
        snippet.setLanguage("java");
        snippet.setTags("java,beginner");

        mockMvc.perform(post("/api/snippets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(snippet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Hello World"))
                .andExpect(jsonPath("$.language").value("java"));
    }

    @Test
    void shouldListAllSnippets() throws Exception {
        Snippet s1 = new Snippet();
        s1.setTitle("Snippet 1");
        s1.setCode("code1");
        s1.setTags("java");
        snippetRepository.save(s1);

        Snippet s2 = new Snippet();
        s2.setTitle("Snippet 2");
        s2.setCode("code2");
        s2.setTags("python");
        snippetRepository.save(s2);

        mockMvc.perform(get("/api/snippets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldFilterByTag() throws Exception {
        Snippet s1 = new Snippet();
        s1.setTitle("Java snippet");
        s1.setCode("code");
        s1.setTags("java,spring");
        snippetRepository.save(s1);

        Snippet s2 = new Snippet();
        s2.setTitle("Python snippet");
        s2.setCode("code");
        s2.setTags("python");
        snippetRepository.save(s2);

        mockMvc.perform(get("/api/snippets").param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Java snippet"));
    }

    @Test
    void shouldDeleteSnippet() throws Exception {
        Snippet s = new Snippet();
        s.setTitle("To delete");
        s.setCode("code");
        s = snippetRepository.save(s);

        mockMvc.perform(delete("/api/snippets/" + s.getId()))
                .andExpect(status().isNoContent());
    }
}
