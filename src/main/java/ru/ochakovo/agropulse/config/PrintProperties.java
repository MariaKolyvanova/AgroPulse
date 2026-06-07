package ru.ochakovo.agropulse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agropulse.print")
public record PrintProperties(
        String baseUrl,
        int timeoutSeconds,
        double deviceScaleFactor,
        double chartDevicePixelRatio
) {
    public PrintProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://127.0.0.1:8080";
        }
        if (timeoutSeconds <= 0) {
            timeoutSeconds = 180;
        }
        deviceScaleFactor = clampScale(deviceScaleFactor, 4.0);
        chartDevicePixelRatio = clampScale(chartDevicePixelRatio, 4.0);
    }

    private static double clampScale(double value, double defaultValue) {
        if (!Double.isFinite(value) || value < 1.0) {
            return defaultValue;
        }
        return Math.min(value, 4.0);
    }
}
