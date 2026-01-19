package com.example.S2S.repository;

import com.example.S2S.entity.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportMessageRepository extends JpaRepository<SupportMessage, String> {
    List<SupportMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}