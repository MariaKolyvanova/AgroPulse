package ru.ochakovo.agropulse.listener;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.repository.UserRepository;
import ru.ochakovo.agropulse.service.audit.AuthAuditService;

@Component
@RequiredArgsConstructor
public class AuthListener {

    private final AuthAuditService authAuditService;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @EventListener
    public void success(AuthenticationSuccessEvent event) {
        saveAuthAttempt(event.getAuthentication() != null ? event.getAuthentication().getName() : null);
    }

    @EventListener
    public void failure(AbstractAuthenticationFailureEvent event) {
        saveAuthAttempt(event.getAuthentication() != null ? event.getAuthentication().getName() : null);
    }

    private void saveAuthAttempt(String principalName) {
        if (principalName == null) {
            return;
        }
        userRepository.findByUsername(principalName)
                .ifPresent(this::saveAudit);
    }

    private void saveAudit(User user) {
        authAuditService.save(
                user.getCompany(),
                user,
                request.getRemoteAddr()
        );
    }
}
