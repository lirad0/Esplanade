package com.esplanade.backend.model;

public record AuthenticationResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username) {
}
