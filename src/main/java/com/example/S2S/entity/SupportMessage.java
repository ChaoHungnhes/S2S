package com.example.S2S.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "support_messages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SupportMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private SupportConversation conversation;

    @Enumerated(EnumType.STRING)
    private Sender sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    public enum Sender {
        USER,
        AI
    }
}
