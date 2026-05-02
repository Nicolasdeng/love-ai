package com.loveai.chat.repository;

import com.loveai.chat.entity.TargetProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TargetProfileRepository extends JpaRepository<TargetProfile, Long> {

    List<TargetProfile> findByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}