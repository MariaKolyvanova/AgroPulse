package ru.ochakovo.agropulse.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.ochakovo.agropulse.entity.data.Month;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.service.organization.CompanyService;
import ru.ochakovo.agropulse.service.organization.UserService;
import ru.ochakovo.agropulse.service.menu.MenuItem;
import ru.ochakovo.agropulse.service.menu.MenuService;
import ru.ochakovo.agropulse.util.RuNumberFormat;

import java.beans.PropertyEditorSupport;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @InitBinder
    public void bindMonth(WebDataBinder binder) {
        binder.registerCustomEditor(Month.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null || text.isBlank() ? null : Month.valueOf(text));
            }
        });
    }

    private final UserService userService;
    private final CompanyService companyService;
    private final MenuService menuService;
    private final RuNumberFormat ruNumbers;

    @ModelAttribute("ruNumbers")
    public RuNumberFormat ruNumbers() {
        return ruNumbers;
    }

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

    @ModelAttribute("parentPath")
    public String parentPath(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String path = request.getRequestURI();
        if (path == null || path.isBlank() || "/".equals(path)) {
            return null;
        }

        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return null;
        }

        return path.substring(0, lastSlash);
    }

}
