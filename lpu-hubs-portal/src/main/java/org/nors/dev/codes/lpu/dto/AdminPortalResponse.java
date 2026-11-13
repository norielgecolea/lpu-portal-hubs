package org.nors.dev.codes.lpu.dto;

import java.util.List;

public record AdminPortalResponse(
        String code,
        String title,
        String subtitle,
        String welcomeLabel,
        String layout,
        List<AdminLinkResponse> links
) {
}
