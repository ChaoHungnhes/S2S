package com.example.S2S.service.Impl;

import com.example.S2S.common.Enum.ErrorCode;
import com.example.S2S.common.Util.CookieUtils;
import com.example.S2S.dto.authDto.AuthenticationRequest;
import com.example.S2S.dto.authDto.AuthenticationResponse;
import com.example.S2S.dto.authDto.IntrospectResponse;
import com.example.S2S.entity.InvalidatedToken;
import com.example.S2S.entity.User;
import com.example.S2S.exception.AppException;
import com.example.S2S.repository.InvalidatedTokenRepository;
import com.example.S2S.repository.UserRepository;
import com.example.S2S.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationImpl implements AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    HttpServletResponse httpServletResponse;
    HttpServletRequest httpServletRequest;
    @NonFinal
    @Value("${application.security.jwt.secret-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${application.security.jwt.expiration}")
    protected int VALID_DURATION;

    @NonFinal
    @Value("${application.security.jwt.refresh-token.expiration}")
    protected int REFRESHABLE_DURATION;

    @NonFinal
    @Value("${application.security.jwt.cookie-auth.name}")
    protected String AUTH;

    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(
                JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer(user.getName().toString())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now()
                                .plus(VALID_DURATION, ChronoUnit.SECONDS)
                                .toEpochMilli()
                )) // time het han
                .jwtID(String.valueOf(UUID.randomUUID()))
                .claim("scope", buildScope(user)) // luu scope gom cac role cua user
                .build();
        Payload payload =
                new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Can not create token");
        }
    }

    // hàm xác thực và giải mã để lấy tt token(đã chuyển đổi từ introspect)
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT
                .getJWTClaimsSet()
                .getExpirationTime();

        var verify = signedJWT.verify(verifier);
        if (!(verify
                && expityTime.after(
                new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner =
                new StringJoiner(" ");
        stringJoiner.add("ROLE_" + user.getRole());
        return stringJoiner.toString();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository
                .findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(
                authenticationRequest.getPassword(),
                user.getPassword());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);

        CookieUtils.setCookie(httpServletResponse, AUTH, token, VALID_DURATION);
        return AuthenticationResponse.builder()
                .token(token)
                .name(user.getName())
                .role(user.getRole().toString())
                .authenticated(authenticated)
                .build();
    }

    @Override
    public IntrospectResponse introspect() {
        var token = CookieUtils.getCookie(httpServletRequest, AUTH);
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public void logout() {
        try {
            String accessToken = CookieUtils.getCookie(httpServletRequest, AUTH);
            var signToken = verifyToken(accessToken, true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
            CookieUtils.clearAuthCookie(httpServletResponse,AUTH);
        } catch (AppException | JOSEException | ParseException e) {
            log.info("token already expired");
        }
    }
    @Override
    public AuthenticationResponse refreshToken() throws ParseException, JOSEException {
        String accessToken = CookieUtils.getCookie(httpServletRequest, AUTH);
        var signedJwt = verifyToken(accessToken, true);
        var jit = signedJwt.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJwt.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
        var email = signedJwt.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        var token = generateToken(user);
        CookieUtils.setCookie(httpServletResponse, AUTH, token, VALID_DURATION);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
}
