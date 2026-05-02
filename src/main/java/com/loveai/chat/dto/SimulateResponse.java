package com.loveai.chat.dto;

import lombok.Data;

@Data
public class SimulateResponse {

    /** AI扮演对方的回复内容 */
    private String reply;

    /** 对用户本轮消息的反馈评价 */
    private String feedback;

    /** 本轮回复质量评分 0-100 */
    private Integer score;
}