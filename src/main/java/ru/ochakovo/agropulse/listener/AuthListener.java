package ru.ochakovo.agropulse.listener;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.repository.UserRepository;
import ru.ochakovo.agropulse.service.AuthAuditService;

@Component
@RequiredArgsConstructor
public class AuthSuccessListener {

    private final AuthAuditService authAuditService;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        Long userId = userRepository.findByUsername(username).orElse(null).getId();

        String ip = request.getRemoteAddr();

        authAuditService.save(userId, username, ip, true);
    }

}
