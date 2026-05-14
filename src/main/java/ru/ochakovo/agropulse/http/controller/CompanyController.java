package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.service.CompanyService;
import ru.ochakovo.agropulse.service.UserService;

@RequestMapping("/companies")
@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final UserService userService;
    private final CompanyService companyService;

    @GetMapping("/main")
    public String mainAdmin(Model model, Authentication auth) {
        UserReadDto currentUser = userService.findByUsername(auth.getName()).orElseThrow();

        model.addAttribute("info", companyService.getInfoByCompanyId(currentUser.companyId()));

        return "companies/main";
    }
}
