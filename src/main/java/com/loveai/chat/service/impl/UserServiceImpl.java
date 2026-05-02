package com.loveai.chat.service.impl;

import com.loveai.chat.dto.UserLoginRequest;
import com.loveai.chat.dto.UserLoginResponse;
import com.loveai.chat.dto.UserRegisterRequest;
import com.loveai.chat.entity.User;
import com.loveai.chat.repository.UserRepository;
import com.loveai.chat.service.UserService;
import com.loveai.chat.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void register(UserRegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("手机号已注册");
        }
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(md5(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : "用户" + request.getPhone().substring(7));
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        userRepository.save(user);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("手机号未注册"));
        if (!user.getPassword().equals(md5(request.getPassword()))) {
            throw new IllegalArgumentException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        return new UserLoginResponse(token, user.getId(), user.getNickname(), user.getPhone());
    }

    @Override
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}