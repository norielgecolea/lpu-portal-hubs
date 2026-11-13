package org.nors.dev.codes.lpu.dto;

import java.util.List;

public record PortalResponse(
        String code,
        String title,
        String subtitle,
        String welcomeLabel,
        String layout,
        List<LinkResponse> links
) {
}
