package com.loveai.chat.service;

import com.loveai.chat.dto.ChatAnalyzeRequest;
import com.loveai.chat.dto.ChatAnalyzeResponse;

public interface ChatAnalysisService {
    ChatAnalyzeResponse analyze(ChatAnalyzeRequest request);
}