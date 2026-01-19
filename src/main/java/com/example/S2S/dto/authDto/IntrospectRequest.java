package com.example.S2S.dto.authDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // tạo obj 1 cách nhanh hơn
@FieldDefaults(level = AccessLevel.PRIVATE)
// class để verify token
public class IntrospectRequest {
    String token;
}
