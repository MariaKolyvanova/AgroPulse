package ru.ochakovo.agropulse.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.entity.organization.UserStatus;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final Long companyId;
    private final Role role;
    private final String username;
    private final String password;
    private final boolean enabled;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.companyId = user.getCompany().getId();
        this.role = user.getRole();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getUserStatus() == UserStatus.ACTIVE;
    }

    public static CustomUserDetails requireCurrent() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        if (!(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new IllegalStateException("Expected CustomUserDetails principal");
        }
        return details;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
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
        return enabled;
    }
}
