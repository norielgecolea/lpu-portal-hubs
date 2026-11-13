package org.nors.dev.codes.lpu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LinkWriteRequest(
        @NotBlank(message = "Name is required") @Size(max = 120) String name,
        @NotBlank(message = "URL is required") @Size(max = 1000) String url,
        @NotBlank(message = "Description is required") @Size(max = 300) String description,
        @NotBlank(message = "Icon is required") @Size(max = 80) String icon,
        @NotNull(message = "Sort order is required") Integer sortOrder,
        @Size(max = 200) String downloadName,
        @NotNull(message = "Network-only flag is required") Boolean networkOnly,
        @NotNull(message = "Enabled flag is required") Boolean enabled
) {
}
