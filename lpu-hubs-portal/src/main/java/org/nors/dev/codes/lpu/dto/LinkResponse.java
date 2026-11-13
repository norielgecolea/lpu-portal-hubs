package org.nors.dev.codes.lpu.dto;

public record LinkResponse(
        Long id,
        String name,
        String url,
        String description,
        String icon,
        String downloadName,
        boolean networkOnly,
        int sortOrder
) {
}
