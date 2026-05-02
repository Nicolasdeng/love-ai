package com.loveai.chat.controller;

import com.loveai.chat.dto.*;
import com.loveai.chat.entity.User;
import com.loveai.chat.service.UserService;
import com.loveai.chat.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return ApiResponse.success(null);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<User> profile() {
        Long userId = UserContext.getUserId();
        User user = userService.getProfile(userId);
        user.setPassword(null); // 不返回密码
        return ApiResponse.success(user);
    }
}