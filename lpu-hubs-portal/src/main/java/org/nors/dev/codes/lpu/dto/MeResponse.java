package org.nors.dev.codes.lpu.dto;

public record MeResponse(String username, String role, boolean passwordChangeRequired) {
}
