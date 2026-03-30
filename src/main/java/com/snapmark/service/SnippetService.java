package com.snapmark.service;

import com.snapmark.model.Snippet;
import com.snapmark.repository.SnippetRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
     * Find snippets by multiple tags with configurable logic (AND/OR).
     * @param tags comma-separated list of tags
     * @param logic "and" or "or" (case-insensitive), defaults to "or" if not specified
     * @return filtered list of snippets
     */
    public List<Snippet> findByTags(String tags, String logic) {
        // 1. 解析输入的标签列表
        List<String> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // 2. 获取所有 snippets
        List<Snippet> allSnippets = findAll();

        // 3. 使用 Stream API 进行过滤
        return allSnippets.stream()
                .filter(snippet -> {
                    // 处理 null 或空标签的情况
                    if (snippet.getTags() == null || snippet.getTags().isBlank()) {
                        return false;
                    }
                    
                    // 解析当前 snippet 的标签
                    List<String> snippetTags = Arrays.stream(snippet.getTags().split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());

                    // 根据 logic 参数选择过滤策略
                    if ("and".equalsIgnoreCase(logic)) {
                        // AND 逻辑：snippet 必须包含所有请求的标签
                        return snippetTags.containsAll(tagList);
                    } else {
                        // OR 逻辑：snippet 只需包含任一请求的标签
                        return tagList.stream().anyMatch(snippetTags::contains);
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
     * Handles null and empty tags gracefully.
     */
    public List<String> getAllTags() {
        List<Snippet> snippets = snippetRepository.findAll();
        return snippets.stream()
                .map(Snippet::getTags)
                .filter(Objects::nonNull)  // 过滤 null 值
                .filter(tags -> !tags.isBlank())  // 过滤空字符串
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())  // 过滤空标签
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
