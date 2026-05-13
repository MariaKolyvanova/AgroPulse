package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.CompanyCreateEditDto;
import ru.ochakovo.agropulse.dto.UserCreateDto;
import ru.ochakovo.agropulse.service.RegistrationService;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final RegistrationService registrationService;

    @PostMapping
    public String create(@ModelAttribute @Validated UserCreateDto user,
                         BindingResult bindingResultUser,
                         @ModelAttribute @Validated CompanyCreateEditDto company,
                         BindingResult bindingResultCompany,
                         RedirectAttributes redirectAttributes){

        if (bindingResultUser.hasErrors() || bindingResultCompany.hasErrors()){
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("userErrors", bindingResultUser.getAllErrors());
            redirectAttributes.addFlashAttribute("companyErrors", bindingResultCompany.getAllErrors());

            return "redirect:/registration";
        }

        registrationService.register(company, user);

        return "redirect:/login";
    }


    @GetMapping("/login")
    public String loginPage(){
        return "user/login";
    }



}
