package com.loveai.chat.service;

import com.loveai.chat.dto.TargetProfileRequest;
import com.loveai.chat.entity.TargetProfile;

import java.util.List;

public interface TargetProfileService {

    TargetProfile create(Long userId, TargetProfileRequest request);

    List<TargetProfile> list(Long userId);

    TargetProfile get(Long userId, Long profileId);

    TargetProfile update(Long userId, Long profileId, TargetProfileRequest request);

    void delete(Long userId, Long profileId);
}