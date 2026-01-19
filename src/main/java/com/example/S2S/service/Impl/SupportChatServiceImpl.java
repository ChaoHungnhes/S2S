package com.example.S2S.service.Impl;

import com.example.S2S.entity.SupportConversation;
import com.example.S2S.entity.SupportMessage;
import com.example.S2S.repository.SupportConversationRepository;
import com.example.S2S.repository.SupportMessageRepository;
import com.example.S2S.service.SupportChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportChatServiceImpl implements SupportChatService {

    private final SupportConversationRepository conversationRepository;
    private final SupportMessageRepository messageRepository;

    @Override
    public SupportMessage sendUserMessage(String conversationId, String content) {

        SupportConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        SupportMessage message = SupportMessage.builder()
                .conversation(conversation)
                .sender(SupportMessage.Sender.USER)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    @Override
    public SupportMessage sendAiResponse(String conversationId, String userMessage) {

        String reply = generateAnswer(userMessage);

        SupportConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        SupportMessage aiMessage = SupportMessage.builder()
                .conversation(conversation)
                .sender(SupportMessage.Sender.AI)
                .content(reply)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(aiMessage);
    }

    private String generateAnswer(String question) {
        if (question.toLowerCase().contains("đăng bài")) {
            return "Bạn có thể đăng bài bằng cách vào mục 'Đăng bán' và điền đầy đủ thông tin sản phẩm.";
        }
        return "Bạn vui lòng mô tả rõ câu hỏi để hệ thống hỗ trợ tốt hơn.";
    }
}

