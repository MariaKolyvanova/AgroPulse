package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.service.UserService;

@Slf4j
@RequestMapping("/users")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String user(Model model, @PathVariable Long id, Authentication auth){

        model.addAttribute("thisUser", userService.findById(id));
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());


        return "/general/user";
    }
}
