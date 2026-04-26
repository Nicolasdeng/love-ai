package com.loveai.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loveai.chat.dto.ChatSuggestionRequest;
import com.loveai.chat.dto.ChatSuggestionResponse;
import com.loveai.chat.prompt.PromptTemplates;
import com.loveai.chat.rag.MarkdownRagManager;
import com.loveai.chat.service.ChatSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSuggestionServiceImpl implements ChatSuggestionService {

    private final ChatClient chatClient;
    private final MarkdownRagManager ragManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatSuggestionResponse getSuggestions(ChatSuggestionRequest request) {
        String prompt = buildEnhancedPrompt(request);
        log.info("发送增强提示词给AI（已集成Markdown RAG）");
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        log.info("收到AI响应");
        return parseResponse(response);
    }

    /**
     * 构建增强的Prompt（集成Markdown RAG检索）
     */
    private String buildEnhancedPrompt(ChatSuggestionRequest request) {
        // 1. 构建对话历史文本
        StringBuilder conversationHistory = new StringBuilder();
        if (request.getMessages() != null && !request.getMessages().isEmpty()) {
            request.getMessages().forEach(msg -> {
                String role = "user".equals(msg.getRole()) ? "我" : "对方";
                conversationHistory.append(role).append(": ").append(msg.getContent()).append("\n");
            });
        }

        // 2. 获取最后一条消息用于RAG检索
        String lastMessage = "";
        if (request.getMessages() != null && !request.getMessages().isEmpty()) {
            lastMessage = request.getMessages().get(request.getMessages().size() - 1).getContent();
        }

        // 3. 从Markdown知识库检索相关内容
        String retrievedContext = "";
        try {
            retrievedContext = ragManager.retrieve(
                    request.getScene(),
                    request.getRelationship(),
                    lastMessage,
                    3  // 检索top 3
            );
            if (!retrievedContext.isEmpty()) {
                log.info("从Markdown知识库检索到相关内容");
            }
        } catch (Exception e) {
            log.warn("Markdown RAG检索失败", e);
        }

        // 4. 使用增强的Prompt模板
        return PromptTemplates.buildEnhancedPrompt(
                getSceneDescription(request.getScene()),
                getRelationshipDescription(request.getRelationship()),
                getGenderDescription(request.getGender()),
                conversationHistory.toString(),
                retrievedContext
        );
    }

    private ChatSuggestionResponse parseResponse(String response) {
        try {
            String jsonStr = extractJson(response);
            log.debug("提取的JSON: {}", jsonStr);
            return objectMapper.readValue(jsonStr, ChatSuggestionResponse.class);
        } catch (Exception e) {
            log.error("解析响应失败，原始响应: {}", response, e);
            throw new RuntimeException("解析AI响应失败: " + e.getMessage(), e);
        }
    }

    private String extractJson(String response) {
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.indexOf("```", start);
            return response.substring(start, end).trim();
        } else if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            int end = response.indexOf("```", start);
            return response.substring(start, end).trim();
        }
        return response.trim();
    }

    private String getSceneDescription(String scene) {
        return switch (scene) {
            case "initial" -> "初次认识";
            case "daily" -> "日常聊天";
            case "date" -> "约会中";
            case "conflict" -> "发生矛盾";
            case "reconciliation" -> "和解";
            default -> "未知场景";
        };
    }

    private String getRelationshipDescription(String relationship) {
        return switch (relationship) {
            case "stranger" -> "陌生人";
            case "friend" -> "朋友";
            case "dating" -> "恋爱中";
            case "couple" -> "情侣";
            case "married" -> "已婚";
            default -> "未知关系";
        };
    }

    private String getGenderDescription(String gender) {
        return switch (gender) {
            case "male" -> "男";
            case "female" -> "女";
            default -> "未知";
        };
    }
}