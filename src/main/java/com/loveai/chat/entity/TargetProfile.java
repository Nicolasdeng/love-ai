package com.loveai.chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "target_profile")
public class TargetProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String nickname;

    /** male / female */
    @Column(length = 10)
    private String gender;

    private Integer age;

    /** 性格特点 */
    @Column(length = 500)
    private String personality;

    /** 兴趣爱好 */
    @Column(length = 500)
    private String interests;

    /** 生日 */
    private LocalDate birthday;

    /** 纪念日，JSON格式 */
    @Column(name = "important_dates", columnDefinition = "TEXT")
    private String importantDates;

    /**
     * 关系状态：stranger / friend / ambiguous / lover
     */
    @Column(name = "relationship_status", length = 20)
    private String relationshipStatus;

    /** 备注 */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}