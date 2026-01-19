package com.example.S2S.entity;

import com.example.S2S.common.Enum.AiStatus;
import com.example.S2S.common.Enum.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private AiStatus aiStatus;

    @Column(columnDefinition = "TEXT")
    private String aiNote;

    private LocalDateTime createdAt;

}

