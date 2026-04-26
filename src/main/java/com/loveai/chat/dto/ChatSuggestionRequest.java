package com.loveai.chat.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatSuggestionRequest {

    /**
     * 聊天场景：initial(初识)、dating(热恋)、conflict(冷战)、daily(日常)
     */
    private String scene;

    /**
     * 对方性别：male(男)、female(女)
     */
    private String gender;

    /**
     * 关系阶段：stranger(陌生人)、friend(朋友)、ambiguous(暧昧)、lover(恋人)
     */
    private String relationship;

    /**
     * 对话消息列表
     */
    private List<Message> messages;

    @Data
    public static class Message {
        /**
         * 角色：user(用户) 或 other(对方)
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;
    }
}