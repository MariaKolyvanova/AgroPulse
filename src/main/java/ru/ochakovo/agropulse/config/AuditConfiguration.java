package ru.ochakovo.agropulse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;


@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.ochakovo.agropulse.repository")
@EnableJpaAuditing
@Configuration
public class AuditConfiguration implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(principal -> {
                    if (principal instanceof UserDetails userDetails) {
                        return userDetails.getUsername();
                    }
                    return principal.toString();
                });
    }
}