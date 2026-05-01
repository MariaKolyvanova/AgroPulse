package ru.ochakovo.agropulse.entity.organization;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMINISTRATOR,
    OPERATOR,
    ANALYTIC;

    @Override
    public @Nullable String getAuthority() {
        return name();
    }
}
