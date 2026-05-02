package com.loveai.chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "suggestion_log")
public class SuggestionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "input_message", columnDefinition = "TEXT")
    private String inputMessage;

    /** JSON格式的建议列表 */
    @Column(columnDefinition = "TEXT")
    private String suggestions;

    /** 用户选择的建议序号 */
    private Integer selected;

    /** 用户反馈 1-5 */
    private Integer feedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}