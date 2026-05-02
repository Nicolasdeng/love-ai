package com.loveai.chat.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ChatAnalyzeRequest {

    @NotEmpty(message = "对话记录不能为空")
    private List<Message> conversation;

    /** 对象档案ID（可选） */
    private Long targetId;

    @Data
    public static class Message {
        /** user / other */
        private String role;
        private String content;
        private String timestamp;
    }
}