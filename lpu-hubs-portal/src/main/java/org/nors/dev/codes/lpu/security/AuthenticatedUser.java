package org.nors.dev.codes.lpu.security;

import java.util.Collection;
import java.util.List;
import org.nors.dev.codes.lpu.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

    private final Long id;
    private final String username;
    private final Role role;
    private final boolean passwordChangeRequired;
    private final int sessionVersion;

    public AuthenticatedUser(
            Long id,
            String username,
            Role role,
            boolean passwordChangeRequired,
            int sessionVersion
    ) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.passwordChangeRequired = passwordChangeRequired;
        this.sessionVersion = sessionVersion;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public int getSessionVersion() {
        return sessionVersion;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
