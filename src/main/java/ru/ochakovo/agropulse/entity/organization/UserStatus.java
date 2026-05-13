package ru.ochakovo.agropulse.entity.organization;

public enum UserStatus {
    ACTIVE("Активен"),
    ARCHIVE("В архиве");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}