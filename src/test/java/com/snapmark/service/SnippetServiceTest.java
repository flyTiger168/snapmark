package com.snapmark.service;

import com.snapmark.model.Snippet;
import com.snapmark.repository.SnippetRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SnippetServiceTest {
    @Test
    void getAllTagsSkipsNullAndEmptyTags() {
        SnippetRepository repository = mock(SnippetRepository.class);
        SnippetService service = new SnippetService(repository);

        Snippet withoutTags = new Snippet();
        withoutTags.setTags(null);

        Snippet emptyTags = new Snippet();
        emptyTags.setTags("   ");

        Snippet tagged = new Snippet();
        tagged.setTags(" java, spring , api ");

        Snippet duplicateTags = new Snippet();
        duplicateTags.setTags("api,, java");

        when(repository.findAll()).thenReturn(List.of(withoutTags, emptyTags, tagged, duplicateTags));

        assertEquals(List.of("api", "java", "spring"), service.getAllTags());
    }
}
