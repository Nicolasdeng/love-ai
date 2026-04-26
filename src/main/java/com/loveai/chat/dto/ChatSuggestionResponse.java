package com.loveai.chat.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatSuggestionResponse {

    private List<Suggestion> suggestions;
    private EmotionAnalysis emotion;

    @Data
    public static class Suggestion {
        private String text;
        private String tone;
    }

    @Data
    public static class EmotionAnalysis {
        private String type;
        private Double intensity;
        private String description;
    }
}