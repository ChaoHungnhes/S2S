package com.example.S2S.service;

import com.example.S2S.dto.AI.AiResult;
import com.example.S2S.entity.Product;

public interface AiModerationService {
    AiResult analyze(Product product);
}
