package com.example.S2S.config;

import com.example.S2S.common.Enum.ErrorCode;
import com.example.S2S.exception.AppException;
import com.example.S2S.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecoder
        implements JwtDecoder {
    @Value("${application.security.jwt.secret-key}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        var response = authenticationService.introspect();

        if (!response.isValid()) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    signerKey.getBytes(),
                    "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(
                            secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
