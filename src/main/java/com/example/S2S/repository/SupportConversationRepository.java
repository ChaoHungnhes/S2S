package com.example.S2S.repository;

import com.example.S2S.entity.SupportConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportConversationRepository extends JpaRepository<SupportConversation, String> {
    List<SupportConversation> findByUserId(String userId);
}