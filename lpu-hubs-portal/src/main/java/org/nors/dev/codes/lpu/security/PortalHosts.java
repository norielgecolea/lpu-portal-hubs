package org.nors.dev.codes.lpu.security;

import java.util.Locale;

/** Maps a public hostname to the Admin Portal or the Learner's Hub. */
public final class PortalHosts {

    private PortalHosts() {
    }

    public static String codeFor(String hostHeader) {
        if (hostHeader == null || hostHeader.isBlank()) {
            return null;
        }
        String host = hostHeader.split(",")[0].trim().toLowerCase(Locale.ROOT);
        int colon = host.lastIndexOf(':');
        if (colon > 0 && host.indexOf(']') < 0) {
            host = host.substring(0, colon);
        }
        if (host.equals("adminportal.lpulaguna.com") || host.startsWith("adminportal.")) {
            return "ADMIN";
        }
        if (host.equals("learnershub.lpulaguna.com") || host.startsWith("learnershub.")) {
            return "LEARNER";
        }
        return null;
    }

    public static String normalize(String code) {
        if (code == null) {
            return null;
        }
        String normalized = code.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("ADMIN") || normalized.equals("LEARNER")) {
            return normalized;
        }
        return null;
    }
}
