package ru.ochakovo.agropulse.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
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

//    @ModelAttribute
//    public void addUserAndCompany(Model model, Authentication auth) {
//
//        if (!(auth.getPrincipal() instanceof CustomUserDetails principal)) {
//            return;
//        }
//
//        model.addAttribute("user", principal);
//
//        if (principal.getCompanyId() != null) {
//            model.addAttribute(
//                    "company",
//                    companyService.findById(principal.getCompanyId())
//                              );
//        }
//    }

    @ModelAttribute
    public void addUserAndCompany(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return;
        }

        String username = auth.getName();

        userService.findByUsername(username).ifPresent(user -> {
            model.addAttribute("user", user);

            if (user.companyId() != null) {
                model.addAttribute("company", companyService.findById(user.companyId()));
            }
        });
    }

    @ModelAttribute("menuItems")
    public List<MenuItem> menuItems(Authentication auth) {
        if (auth == null)
            return null;

        String role = auth.getAuthorities().iterator().next().getAuthority();
        return menuService.getMenu(role);
    }

    @ModelAttribute("requestPath")
    public String requestPath(HttpServletRequest request) {
        if (request == null)
            return null;

        return request.getRequestURI();
    }

}
