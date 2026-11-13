package org.nors.dev.codes.lpu.security;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class LoginRateLimiter {

    private static final int MAX_FAILURES = 8;
    private static final long WINDOW_MS = 15 * 60_000L;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public void check(String key) {
        Window window = windows.computeIfAbsent(key, ignored -> new Window());
        synchronized (window) {
            long now = System.currentTimeMillis();
            if (now - window.start > WINDOW_MS) {
                window.start = now;
                window.failures = 0;
            }
            if (window.failures >= MAX_FAILURES) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too many sign-in attempts. Try again in a few minutes."
                );
            }
        }
    }

    public void onFailure(String key) {
        Window window = windows.computeIfAbsent(key, ignored -> new Window());
        synchronized (window) {
            long now = System.currentTimeMillis();
            if (now - window.start > WINDOW_MS) {
                window.start = now;
                window.failures = 0;
            }
            window.failures++;
        }
    }

    public void onSuccess(String key) {
        windows.remove(key);
    }

    private static final class Window {
        private long start = System.currentTimeMillis();
        private int failures;
    }
}
