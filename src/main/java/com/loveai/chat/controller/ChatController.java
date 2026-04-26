package com.loveai.chat.controller;

import com.loveai.chat.dto.ApiResponse;
import com.loveai.chat.dto.ChatSuggestionRequest;
import com.loveai.chat.dto.ChatSuggestionResponse;
import com.loveai.chat.service.ChatSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSuggestionService chatSuggestionService;

    /**
     * 获取聊天回复建议
     */
    @PostMapping("/suggest")
    public ApiResponse<ChatSuggestionResponse> getSuggestions(
            @RequestBody ChatSuggestionRequest request) {
        try {
            log.info("收到建议请求: {}", request);
            ChatSuggestionResponse response = chatSuggestionService.getSuggestions(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取建议失败", e);
            return ApiResponse.error("获取建议失败: " + e.getMessage());
        }
    }
}