package org.nors.dev.codes.lpu.security;

import java.util.List;

/** Browser origins allowed to call the API from a separate dev server. */
public final class CorsOrigins {

    public static final List<String> PATTERNS = List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://learnershub.lpulaguna.com",
            "https://adminportal.lpulaguna.com"
    );

    private CorsOrigins() {
    }
}
