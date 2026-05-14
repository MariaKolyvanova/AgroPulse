package ru.ochakovo.agropulse.http.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static ru.ochakovo.agropulse.entity.organization.Role.*;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        var authorities = authentication.getAuthorities();
        String redirectUrl = "/";

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ADMINISTRATOR.getAuthority()))) {
            redirectUrl = "/companies/main";
        }
        else if (authorities.stream().anyMatch(a -> a.getAuthority().equals(OPERATOR.getAuthority()))) {
            redirectUrl = "/reports/create";
        }
        else if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ANALYTIC.getAuthority()))) {
            redirectUrl = "/reports/calculation";
        }

        response.sendRedirect(redirectUrl);
    }
}
