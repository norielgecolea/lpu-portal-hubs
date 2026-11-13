package org.nors.dev.codes.lpu.service;

import java.time.Instant;
import org.nors.dev.codes.lpu.dto.ChangePasswordRequest;
import org.nors.dev.codes.lpu.dto.LoginRequest;
import org.nors.dev.codes.lpu.dto.LoginResponse;
import org.nors.dev.codes.lpu.dto.MeResponse;
import org.nors.dev.codes.lpu.model.User;
import org.nors.dev.codes.lpu.repository.UserRepository;
import org.nors.dev.codes.lpu.security.AuthenticatedUser;
import org.nors.dev.codes.lpu.security.LoginRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginRateLimiter loginRateLimiter;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            LoginRateLimiter loginRateLimiter
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginRateLimiter = loginRateLimiter;
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String clientKey) {
        String username = request.username().trim();
        String rateKey = clientKey + "|" + username.toLowerCase();
        loginRateLimiter.check(rateKey);

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            loginRateLimiter.onFailure(rateKey);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        loginRateLimiter.onSuccess(rateKey);
        boolean rememberMe = Boolean.TRUE.equals(request.rememberMe());
        return toResponse(user, rememberMe);
    }

    @Transactional
    public LoginResponse changePassword(AuthenticatedUser principal, ChangePasswordRequest request) {
        User user = requiredUser(principal);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a password that is different from the current one");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangeRequired(false);
        user.rotateSession();
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        return toResponse(user, false);
    }

    @Transactional
    public void logout(AuthenticatedUser principal) {
        if (principal == null) {
            return;
        }
        userRepository.findById(principal.getId()).ifPresent(user -> {
            user.rotateSession();
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        });
    }

    @Transactional(readOnly = true)
    public MeResponse me(AuthenticatedUser principal) {
        User user = requiredUser(principal);
        return new MeResponse(user.getUsername(), user.getRole().name(), user.isPasswordChangeRequired());
    }

    private User requiredUser(AuthenticatedUser principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findById(principal.getId())
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
    }

    private LoginResponse toResponse(User user, boolean rememberMe) {
        return new LoginResponse(
                jwtService.generateToken(user, rememberMe),
                user.getUsername(),
                user.getRole().name(),
                user.isPasswordChangeRequired(),
                jwtService.getExpirationMs(rememberMe)
        );
    }
}
