package org.nors.dev.codes.lpu.dto;

public record LoginResponse(
        String token,
        String username,
        String role,
        boolean passwordChangeRequired,
        long expiresInMs
) {
}
