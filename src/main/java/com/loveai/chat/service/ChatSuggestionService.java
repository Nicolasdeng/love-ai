package com.loveai.chat.service;

import com.loveai.chat.dto.ChatSuggestionRequest;
import com.loveai.chat.dto.ChatSuggestionResponse;

/**
 * 聊天建议服务接口
 */
public interface ChatSuggestionService {

    /**
     * 获取聊天回复建议
     *
     * @param request 请求参数
     * @return 回复建议响应
     */
    ChatSuggestionResponse getSuggestions(ChatSuggestionRequest request);
}