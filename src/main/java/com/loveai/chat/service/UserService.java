package com.loveai.chat.service;

import com.loveai.chat.dto.UserLoginRequest;
import com.loveai.chat.dto.UserLoginResponse;
import com.loveai.chat.dto.UserRegisterRequest;
import com.loveai.chat.entity.User;

public interface UserService {

    void register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);

    User getProfile(Long userId);
}