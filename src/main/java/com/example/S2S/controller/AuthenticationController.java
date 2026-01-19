package com.example.S2S.controller;

import com.example.S2S.dto.authDto.AuthenticationRequest;
import com.example.S2S.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/s2s/authentication")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/introspect")
    public ResponseEntity<Object> authenticate() {
        var result = authenticationService.introspect();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(){
        authenticationService.logout();
        return ResponseEntity.ok("logout success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh()
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken();
        return ResponseEntity.ok(result);
    }
}

