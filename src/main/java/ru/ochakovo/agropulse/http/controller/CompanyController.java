package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ochakovo.agropulse.service.CompanyService;

@RequestMapping("/companies")
@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/main")
    public String mainAdmin(Model model) {

        model.addAttribute("info", companyService.getInfo());

        return "companies/main";
    }
}
