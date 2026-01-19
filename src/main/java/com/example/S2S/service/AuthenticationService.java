package com.example.S2S.service;

import com.example.S2S.dto.authDto.*;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    public IntrospectResponse introspect();
    public void logout();
    public AuthenticationResponse refreshToken() throws ParseException, JOSEException;

}
