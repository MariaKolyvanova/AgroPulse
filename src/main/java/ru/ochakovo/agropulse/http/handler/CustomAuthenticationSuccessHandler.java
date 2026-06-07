package ru.ochakovo.agropulse.http.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.ochakovo.agropulse.security.CustomUserDetails;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/";

        if (authentication.getPrincipal() instanceof CustomUserDetails details) {
            redirectUrl = switch (details.getRole()) {
                case ADMINISTRATOR -> "/companies/main";
                case OPERATOR -> "/reports/create";
                case ANALYTIC -> "/reports";
            };
        }

        response.sendRedirect(redirectUrl);
    }
}
