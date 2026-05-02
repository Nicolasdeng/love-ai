package com.loveai.chat.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatAnalyzeResponse {

    /** 对话情绪走向 */
    private String emotionTrend;

    /** 沟通质量评分 0-100 */
    private Integer qualityScore;

    /** 沟通问题诊断 */
    private List<String> problems;

    /** 改进建议 */
    private List<String> suggestions;

    /** 关键转折点 */
    private List<TurningPoint> turningPoints;

    @Data
    public static class TurningPoint {
        private Integer index;
        private String content;
        private String reason;
    }
}