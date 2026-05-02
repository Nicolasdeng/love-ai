package com.loveai.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loveai.chat.dto.ChatAnalyzeRequest;
import com.loveai.chat.dto.ChatAnalyzeResponse;
import com.loveai.chat.prompt.PromptTemplates;
import com.loveai.chat.service.ChatAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAnalysisServiceImpl implements ChatAnalysisService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatAnalyzeResponse analyze(ChatAnalyzeRequest request) {
        String conversationText = buildConversationText(request);
        String prompt = PromptTemplates.buildAnalyzePrompt(conversationText);

        log.info("发送对话分析请求，对话条数: {}", request.getConversation().size());
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseResponse(response);
    }

    private String buildConversationText(ChatAnalyzeRequest request) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (ChatAnalyzeRequest.Message msg : request.getConversation()) {
            String role = "user".equals(msg.getRole()) ? "我" : "对方";
            sb.append("[").append(index++).append("] ")
              .append(role).append(": ")
              .append(msg.getContent());
            if (msg.getTimestamp() != null) {
                sb.append(" (").append(msg.getTimestamp()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private ChatAnalyzeResponse parseResponse(String response) {
        try {
            String json = extractJson(response);
            return objectMapper.readValue(json, ChatAnalyzeResponse.class);
        } catch (Exception e) {
            log.error("解析对话分析响应失败: {}", response, e);
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
}