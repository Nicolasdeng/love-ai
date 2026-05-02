package com.loveai.chat.controller;

import com.loveai.chat.dto.ApiResponse;
import com.loveai.chat.dto.TargetProfileRequest;
import com.loveai.chat.entity.TargetProfile;
import com.loveai.chat.service.TargetProfileService;
import com.loveai.chat.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/target")
@RequiredArgsConstructor
public class TargetProfileController {

    private final TargetProfileService targetProfileService;

    /**
     * 创建档案
     */
    @PostMapping("/create")
    public ApiResponse<TargetProfile> create(@Valid @RequestBody TargetProfileRequest request) {
        Long userId = UserContext.getUserId();
        TargetProfile profile = targetProfileService.create(userId, request);
        return ApiResponse.success(profile);
    }

    /**
     * 档案列表
     */
    @GetMapping("/list")
    public ApiResponse<List<TargetProfile>> list() {
        Long userId = UserContext.getUserId();
        return ApiResponse.success(targetProfileService.list(userId));
    }

    /**
     * 档案详情
     */
    @GetMapping("/{id}")
    public ApiResponse<TargetProfile> get(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        return ApiResponse.success(targetProfileService.get(userId, id));
    }

    /**
     * 更新档案
     */
    @PutMapping("/{id}")
    public ApiResponse<TargetProfile> update(@PathVariable Long id,
                                              @Valid @RequestBody TargetProfileRequest request) {
        Long userId = UserContext.getUserId();
        return ApiResponse.success(targetProfileService.update(userId, id, request));
    }

    /**
     * 删除档案
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        targetProfileService.delete(userId, id);
        return ApiResponse.success(null);
    }
}