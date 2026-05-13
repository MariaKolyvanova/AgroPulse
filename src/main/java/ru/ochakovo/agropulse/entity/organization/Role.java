package ru.ochakovo.agropulse.entity.organization;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMINISTRATOR("Системный администратор"),
    OPERATOR("Оператор данных"),
    ANALYTIC("Финансовый аналитик");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public @Nullable String getAuthority() {
        return name();
    }
}
