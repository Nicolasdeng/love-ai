package com.loveai.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class SimulateRequest {

    /**
     * 模拟场景：confession(初次表白)、date_invite(约会邀请)、conflict(矛盾化解)
     *           comfort(情绪安抚)、surprise(制造惊喜)
     */
    @NotBlank(message = "场景不能为空")
    private String scenario;

    /** 对方性别 male/female */
    private String targetGender;

    /** 关系阶段 */
    private String relationship;

    /** 对方性格（可选，来自档案） */
    private String targetPersonality;

    /** 对话历史（多轮） */
    private List<Message> history;

    /** 用户本轮发送的消息 */
    @NotBlank(message = "消息不能为空")
    private String userMessage;

    @Data
    public static class Message {
        /** user / assistant */
        private String role;
        private String content;
    }
}