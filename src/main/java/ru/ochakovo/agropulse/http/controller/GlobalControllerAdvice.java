package ru.ochakovo.agropulse.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.service.CompanyService;
import ru.ochakovo.agropulse.service.UserService;
import ru.ochakovo.agropulse.service.menu.MenuItem;
import ru.ochakovo.agropulse.service.menu.MenuService;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;
    private final CompanyService companyService;
    private final MenuService menuService;

    @ModelAttribute
    public void addUserAndCompany(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return;
        }

        if (!(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            return;
        }

        var user = userService.findById(principal.getUserId());
        model.addAttribute("user", user);

        if (user.companyId() != null) {
            model.addAttribute("company", companyService.find());
        }
    }

    @ModelAttribute("menuItems")
    public List<MenuItem> menuItems(Authentication auth) {
        if (auth == null) {
            return null;
        }

        if (!(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            return null;
        }

        return menuService.getMenu(principal.getRole().name());
    }

    @ModelAttribute("requestPath")
    public String requestPath(HttpServletRequest request) {
        if (request == null)
            return null;

        return request.getRequestURI();
    }

}
