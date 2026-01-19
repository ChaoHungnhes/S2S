package com.example.S2S.service;

import com.example.S2S.entity.SupportMessage;

public interface SupportChatService {
    SupportMessage sendUserMessage(String conversationId, String content);

    SupportMessage sendAiResponse(String conversationId, String userMessage);
}
