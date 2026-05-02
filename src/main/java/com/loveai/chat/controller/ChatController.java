package com.loveai.chat.controller;

import com.loveai.chat.dto.*;
import com.loveai.chat.service.ChatAnalysisService;
import com.loveai.chat.service.ChatSimulateService;
import com.loveai.chat.service.ChatSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSuggestionService chatSuggestionService;
    private final ChatAnalysisService chatAnalysisService;
    private final ChatSimulateService chatSimulateService;

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

    /**
     * 对话分析
     */
    @PostMapping("/analyze")
    public ApiResponse<ChatAnalyzeResponse> analyze(
            @Valid @RequestBody ChatAnalyzeRequest request) {
        try {
            log.info("收到对话分析请求，对话条数: {}", request.getConversation().size());
            ChatAnalyzeResponse response = chatAnalysisService.analyze(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("对话分析失败", e);
            return ApiResponse.error("对话分析失败: " + e.getMessage());
        }
    }

    /**
     * 情境模拟
     */
    @PostMapping("/simulate")
    public ApiResponse<SimulateResponse> simulate(
            @Valid @RequestBody SimulateRequest request) {
        try {
            log.info("收到情境模拟请求，场景: {}", request.getScenario());
            SimulateResponse response = chatSimulateService.simulate(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("情境模拟失败", e);
            return ApiResponse.error("情境模拟失败: " + e.getMessage());
        }
    }
}