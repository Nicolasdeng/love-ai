package com.loveai.chat.repository;

import com.loveai.chat.entity.ChatHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByUserIdAndTargetIdOrderByCreatedAtAsc(Long userId, Long targetId);

    List<ChatHistory> findByUserIdAndTargetIdOrderByCreatedAtDesc(Long userId, Long targetId, Pageable pageable);
}