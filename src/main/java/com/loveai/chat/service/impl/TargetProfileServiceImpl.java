package com.loveai.chat.service.impl;

import com.loveai.chat.dto.TargetProfileRequest;
import com.loveai.chat.entity.TargetProfile;
import com.loveai.chat.repository.TargetProfileRepository;
import com.loveai.chat.service.TargetProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetProfileServiceImpl implements TargetProfileService {

    private final TargetProfileRepository repository;

    @Override
    public TargetProfile create(Long userId, TargetProfileRequest request) {
        TargetProfile profile = new TargetProfile();
        copyProperties(request, profile);
        profile.setUserId(userId);
        return repository.save(profile);
    }

    @Override
    public List<TargetProfile> list(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public TargetProfile get(Long userId, Long profileId) {
        TargetProfile profile = repository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("档案不存在"));
        if (!profile.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权访问该档案");
        }
        return profile;
    }

    @Override
    public TargetProfile update(Long userId, Long profileId, TargetProfileRequest request) {
        TargetProfile profile = get(userId, profileId);
        copyProperties(request, profile);
        return repository.save(profile);
    }

    @Override
    public void delete(Long userId, Long profileId) {
        TargetProfile profile = get(userId, profileId);
        repository.delete(profile);
    }

    private void copyProperties(TargetProfileRequest request, TargetProfile profile) {
        profile.setNickname(request.getNickname());
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setPersonality(request.getPersonality());
        profile.setInterests(request.getInterests());
        profile.setBirthday(request.getBirthday());
        profile.setImportantDates(request.getImportantDates());
        profile.setRelationshipStatus(request.getRelationshipStatus());
        profile.setNotes(request.getNotes());
    }
}