package org.nors.dev.codes.lpu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PortalWriteRequest(
        @NotBlank(message = "Title is required") @Size(max = 120) String title,
        @NotBlank(message = "Subtitle is required") @Size(max = 300) String subtitle,
        @NotBlank(message = "Welcome label is required") @Size(max = 80) String welcomeLabel
) {
}
