package ru.ochakovo.agropulse.http.controller.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.company.CompanyCreateEditDto;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.service.organization.RegistrationService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    @GetMapping("/registration")
    public String registration(Model model,
                               @ModelAttribute CompanyCreateEditDto company,
                               @ModelAttribute UserCreateDto user){

        model.addAttribute("user", user);
        model.addAttribute("company", company);

        return "auth/registration";
    }

    @PostMapping("/registration")
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
    public String login(){
        return "auth/login";
    }

}
