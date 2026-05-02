package com.loveai.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TargetProfileRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /** male / female */
    private String gender;

    private Integer age;

    private String personality;

    private String interests;

    private LocalDate birthday;

    private String importantDates;

    /**
     * 关系状态：stranger / friend / ambiguous / lover
     */
    private String relationshipStatus;

    private String notes;
}