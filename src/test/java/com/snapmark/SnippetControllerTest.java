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

    @Test
    void shouldFilterByMultipleTagsWithOrLogic() throws Exception {
        Snippet s1 = new Snippet();
        s1.setTitle("Java Spring Snippet");
        s1.setCode("code1");
        s1.setTags("java,spring");
        snippetRepository.save(s1);

        Snippet s2 = new Snippet();
        s2.setTitle("Python Django Snippet");
        s2.setCode("code2");
        s2.setTags("python,django");
        snippetRepository.save(s2);

        Snippet s3 = new Snippet();
        s3.setTitle("Java Only Snippet");
        s3.setCode("code3");
        s3.setTags("java");
        snippetRepository.save(s3);

        // OR logic: should return snippets with EITHER java OR spring
        mockMvc.perform(get("/api/snippets")
                        .param("tags", "java,python")
                        .param("logic", "or"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Java Spring Snippet",
                        "Python Django Snippet",
                        "Java Only Snippet"
                )));
    }

    @Test
    void shouldFilterByMultipleTagsWithAndLogic() throws Exception {
        Snippet s1 = new Snippet();
        s1.setTitle("Java Spring Snippet");
        s1.setCode("code1");
        s1.setTags("java,spring");
        snippetRepository.save(s1);

        Snippet s2 = new Snippet();
        s2.setTitle("Python Django Snippet");
        s2.setCode("code2");
        s2.setTags("python,django");
        snippetRepository.save(s2);

        Snippet s3 = new Snippet();
        s3.setTitle("Java Only Snippet");
        s3.setCode("code3");
        s3.setTags("java");
        snippetRepository.save(s3);

        // AND logic: should return snippets with BOTH java AND spring
        mockMvc.perform(get("/api/snippets")
                        .param("tags", "java,spring")
                        .param("logic", "and"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Java Spring Snippet"));
    }

    @Test
    void shouldDefaultToOrLogicWhenNotSpecified() throws Exception {
        Snippet s1 = new Snippet();
        s1.setTitle("Java Snippet");
        s1.setCode("code1");
        s1.setTags("java");
        snippetRepository.save(s1);

        Snippet s2 = new Snippet();
        s2.setTitle("Python Snippet");
        s2.setCode("code2");
        s2.setTags("python");
        snippetRepository.save(s2);

        // Default behavior should be OR logic
        mockMvc.perform(get("/api/snippets")
                        .param("tags", "java,python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldMaintainBackwardCompatibilityWithSingleTag() throws Exception {
        Snippet s1 = new Snippet();
        s1.setTitle("Java Snippet 1");
        s1.setCode("code1");
        s1.setTags("java,spring");
        snippetRepository.save(s1);

        Snippet s2 = new Snippet();
        s2.setTitle("Java Snippet 2");
        s2.setCode("code2");
        s2.setTags("java");
        snippetRepository.save(s2);

        Snippet s3 = new Snippet();
        s3.setTitle("Python Snippet");
        s3.setCode("code3");
        s3.setTags("python");
        snippetRepository.save(s3);

        // Old single tag parameter should still work
        mockMvc.perform(get("/api/snippets")
                        .param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Java Snippet 1",
                        "Java Snippet 2"
                )));
    }
}
