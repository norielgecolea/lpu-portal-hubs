package org.nors.dev.codes.lpu.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.nors.dev.codes.lpu.dto.ApiError;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PasswordChangeRequiredFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public PasswordChangeRequiredFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.getPrincipal() instanceof AuthenticatedUser user
                && user.isPasswordChangeRequired()
                && !isAllowed(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), new ApiError(
                    Instant.now(),
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden",
                    "Change the initial password before continuing",
                    request.getRequestURI()
            ));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static boolean isAllowed(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/api/auth/change-password")
                || path.contains("/api/auth/logout")
                || path.contains("/api/auth/me");
    }
}
