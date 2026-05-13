package ru.ochakovo.agropulse.http.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.PageResponse;
import ru.ochakovo.agropulse.dto.audit.*;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.service.UserService;
import ru.ochakovo.agropulse.service.audit.AuthAuditService;
import ru.ochakovo.agropulse.service.audit.ReportAuditService;
import ru.ochakovo.agropulse.service.audit.UserAuditService;


@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthAuditService authAuditService;
    private final UserAuditService userAuditService;
    private final ReportAuditService reportAuditService;

    @GetMapping("/main")
    public String mainAdmin() {

        return "admin/main";
    }

    @GetMapping("/users")
    public String users(Model model, Authentication auth) {
        UserReadDto currentUser = userService.findByUsername(auth.getName()).orElseThrow();

        model.addAttribute("users", userService.findAll(currentUser.companyId()));

        return "admin/users";
    }


    @GetMapping("/newUser")
    public String newUser(Model model,
                          @ModelAttribute UserCreateDto newUser
                         ){

        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());

        return "/admin/newUser";
    }


    @GetMapping("/audit")
    public String audit(Model model,
                        AuthAuditFilter authAuditFilter,
                        UserAuditFilter userAuditFilter,
                        ReportAuditFilter reportAuditFilter,
                        Pageable pageable,
                        @RequestParam(defaultValue = "reports") String tab) {

        model.addAttribute("tab", tab);

        switch (tab) {
            case "reports" -> {
                Page<ReportAuditReadDto> page = reportAuditService.findAll(reportAuditFilter, pageable);
                model.addAttribute("reportAudits", PageResponse.of(page));
            }
            case "auth" -> {
                Page<AuthAuditReadDto> page = authAuditService.findAll(authAuditFilter, pageable);
                model.addAttribute("authAudits", PageResponse.of(page));
            }
            case "users" -> {
                Page<UserAuditReadDto> page = userAuditService.findAll(userAuditFilter, pageable);
                model.addAttribute("userAudits", PageResponse.of(page));
            }
        }
        model.addAttribute("reportAuditFilter", reportAuditFilter);
        model.addAttribute("authAuditFilter", authAuditFilter);
        model.addAttribute("userAuditFilter", userAuditFilter);

        return "admin/audit";
    }


    @PostMapping("/newUser")
    public String newUser(@ModelAttribute @Validated UserCreateDto newUser,
                          BindingResult bindingResultUser,
                          RedirectAttributes redirectAttributes,
                          Authentication auth
                         ){

        if (bindingResultUser.hasErrors()){
            redirectAttributes.addFlashAttribute("newUser", newUser);
            redirectAttributes.addFlashAttribute("userErrors", bindingResultUser.getAllErrors());
            return "redirect:/users/newUser";
        }

        Long companyId = userService.findByUsername(auth.getName()).get().companyId();

        userService.create(newUser, companyId);

        Long userId = userService.findByUsername(newUser.username()).get().id();

        return "redirect:/users/" + userId;
    }
}







