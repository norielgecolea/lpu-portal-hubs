package org.nors.dev.codes.lpu.security;

import java.net.URI;

/** Accepts http(s) links and same-site paths such as the LIT download. */
public final class LinkUrls {

    private LinkUrls() {
    }

    public static void check(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Link URL is required");
        }
        String trimmed = url.trim();
        if (trimmed.startsWith("/") && !trimmed.startsWith("//") && !trimmed.contains("\\")) {
            return;
        }
        URI uri;
        try {
            uri = URI.create(trimmed);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Link URL is not valid");
        }
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new IllegalArgumentException("Link URL must be http, https, or a site path");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("Link URL is not valid");
        }
    }
}
