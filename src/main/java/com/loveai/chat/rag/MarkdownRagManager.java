package com.loveai.chat.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 基于Markdown文档的RAG管理器
 * 从resources/knowledge目录读取MD文件作为知识库
 */
@Slf4j
@Component
public class MarkdownRagManager {

    private final List<KnowledgeSection> knowledgeBase = new ArrayList<>();
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @PostConstruct
    public void init() {
        loadMarkdownKnowledge();
        log.info("Markdown RAG初始化完成，共加载{}条知识", knowledgeBase.size());
    }

    /**
     * 检索相关知识
     */
    public String retrieve(String scene, String relationship, String query, int topK) {
        try {
            // 1. 过滤匹配场景和关系的知识
            List<ScoredSection> scored = knowledgeBase.stream()
                    .filter(section -> matchesContext(section, scene, relationship))
                    .map(section -> new ScoredSection(section, calculateScore(section, query)))
                    .filter(ss -> ss.score > 0)
                    .sorted((a, b) -> Double.compare(b.score, a.score))
                    .limit(topK)
                    .collect(Collectors.toList());

            // 2. 格式化返回
            return formatResults(scored);

        } catch (Exception e) {
            log.error("检索失败", e);
            return "";
        }
    }

    /**
     * 加载Markdown知识库
     */
    private void loadMarkdownKnowledge() {
        try {
            Resource[] resources = resolver.getResources("classpath:knowledge/*.md");
            log.info("找到{}个Markdown知识文件", resources.length);

            for (Resource resource : resources) {
                parseMarkdownFile(resource);
            }

        } catch (Exception e) {
            log.error("加载Markdown知识库失败", e);
        }
    }

    /**
     * 解析Markdown文件
     */
    private void parseMarkdownFile(Resource resource) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String fileName = resource.getFilename();
            log.info("解析文件: {}", fileName);

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            // 按二级标题分割章节
            String[] sections = content.toString().split("(?=## )");

            for (String section : sections) {
                if (section.trim().isEmpty()) continue;

                KnowledgeSection ks = parseSection(section, fileName);
                if (ks != null) {
                    knowledgeBase.add(ks);
                }
            }

        } catch (Exception e) {
            log.error("解析Markdown文件失败: {}", resource.getFilename(), e);
        }
    }

    /**
     * 解析章节
     */
    private KnowledgeSection parseSection(String sectionText, String fileName) {
        try {
            KnowledgeSection section = new KnowledgeSection();
            section.setSourceFile(fileName);

            // 提取标题（## 开头）
            Pattern titlePattern = Pattern.compile("^##\\s+(.+)$", Pattern.MULTILINE);
            Matcher titleMatcher = titlePattern.matcher(sectionText);
            if (titleMatcher.find()) {
                section.setTitle(titleMatcher.group(1).trim());
            }

            // 提取元数据
            section.setScene(extractMetadata(sectionText, "场景"));
            section.setRelationship(extractMetadata(sectionText, "关系"));
            section.setKeywords(extractKeywords(sectionText));

            // 提取内容（去除元数据部分）
            String content = sectionText.replaceAll("\\*\\*场景\\*\\*:.*", "")
                    .replaceAll("\\*\\*关系\\*\\*:.*", "")
                    .replaceAll("\\*\\*关键词\\*\\*:.*", "")
                    .trim();
            section.setContent(content);

            return section;

        } catch (Exception e) {
            log.error("解析章节失败", e);
            return null;
        }
    }

    /**
     * 提取元数据
     */
    private String extractMetadata(String text, String key) {
        Pattern pattern = Pattern.compile("\\*\\*" + key + "\\*\\*:\\s*([^\\n]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * 提取关键词
     */
    private List<String> extractKeywords(String text) {
        String keywordsStr = extractMetadata(text, "关键词");
        if (keywordsStr.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(keywordsStr.split("[,，]"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * 匹配上下文
     */
    private boolean matchesContext(KnowledgeSection section, String scene, String relationship) {
        boolean sceneMatch = scene == null || scene.isEmpty() ||
                           section.getScene().isEmpty() ||
                           section.getScene().contains(scene) ||
                           section.getScene().contains("all");

        boolean relationMatch = relationship == null || relationship.isEmpty() ||
                              section.getRelationship().isEmpty() ||
                              section.getRelationship().contains(relationship) ||
                              section.getRelationship().contains("all");

        return sceneMatch && relationMatch;
    }

    /**
     * 计算相关性得分
     */
    private double calculateScore(KnowledgeSection section, String query) {
        if (query == null || query.isEmpty()) {
            return 1.0;
        }

        double score = 0.0;
        String lowerQuery = query.toLowerCase();

        // 1. 关键词匹配（权重最高）
        for (String keyword : section.getKeywords()) {
            if (lowerQuery.contains(keyword.toLowerCase())) {
                score += 3.0;
            }
        }

        // 2. 标题匹配
        if (section.getTitle() != null &&
            section.getTitle().toLowerCase().contains(lowerQuery)) {
            score += 2.0;
        }

        // 3. 内容匹配
        if (section.getContent() != null &&
            section.getContent().toLowerCase().contains(lowerQuery)) {
            score += 1.0;
        }

        return score;
    }

    /**
     * 格式化结果
     */
    private String formatResults(List<ScoredSection> scored) {
        if (scored.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < scored.size(); i++) {
            KnowledgeSection section = scored.get(i).section;
            result.append("【参考").append(i + 1).append("】")
                  .append(section.getTitle()).append("\n");
            result.append(section.getContent()).append("\n\n");
        }

        return result.toString();
    }

    /**
     * 知识章节
     */
    private static class KnowledgeSection {
        private String sourceFile;
        private String title;
        private String scene;
        private String relationship;
        private List<String> keywords = new ArrayList<>();
        private String content;

        public String getSourceFile() { return sourceFile; }
        public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getScene() { return scene; }
        public void setScene(String scene) { this.scene = scene; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    /**
     * 评分章节
     */
    private static class ScoredSection {
        KnowledgeSection section;
        double score;

        ScoredSection(KnowledgeSection section, double score) {
            this.section = section;
            this.score = score;
        }
    }
}