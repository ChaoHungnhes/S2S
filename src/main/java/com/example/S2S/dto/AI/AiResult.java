package com.example.S2S.dto.AI;

import com.example.S2S.common.Enum.AiStatus;

public record AiResult(
        AiStatus status,
        String note
) {
}
