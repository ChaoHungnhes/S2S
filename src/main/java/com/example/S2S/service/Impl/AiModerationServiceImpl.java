package com.example.S2S.service.Impl;

import com.example.S2S.common.Enum.AiStatus;
import com.example.S2S.dto.AI.AiResult;
import com.example.S2S.entity.Product;
import com.example.S2S.service.AiModerationService;
import org.springframework.stereotype.Service;

@Service
public class AiModerationServiceImpl implements AiModerationService {

    @Override
    public AiResult analyze(Product product) {

        if (product.getDescription() == null
                || product.getDescription().length() < 30) {
            return new AiResult(
                    AiStatus.WARNING,
                    "Mô tả sản phẩm còn ngắn, nên bổ sung thêm thông tin."
            );
        }

        return new AiResult(
                AiStatus.OK,
                "Nội dung đầy đủ, không phát hiện dấu hiệu spam."
        );
    }
}

