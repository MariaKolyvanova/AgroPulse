package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.dto.user.UserUpdateDto;
import ru.ochakovo.agropulse.entity.organization.Role;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.security.CustomUserDetails;
import ru.ochakovo.agropulse.service.UserService;

@Slf4j
@RequestMapping("/users")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String user(Model model, @PathVariable Long id){

        model.addAttribute("thisUser", userService.findById(id));
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());


        return "users/user";
    }

    @GetMapping
    public String users(Model model) {

        model.addAttribute("users", userService.findAll(CustomUserDetails.requireCurrent().getCompanyId()));

        return "users/users";
    }

    @GetMapping("/create")
    public String create(Model model, @ModelAttribute UserCreateDto newUser) {

        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());

        return "users/create";
    }

    @PostMapping("/newUser")
    public String create(@ModelAttribute @Validated UserCreateDto newUser, BindingResult bindingResultUser,
                         RedirectAttributes redirectAttributes) {

        if (bindingResultUser.hasErrors()) {
            redirectAttributes.addFlashAttribute("newUser", newUser);
            redirectAttributes.addFlashAttribute("userErrors", bindingResultUser.getAllErrors());
            return "redirect:/users/create";
        }

        Long companyId = CustomUserDetails.requireCurrent().getCompanyId();

        userService.create(newUser, companyId);
        Long userId = userService.findByUsername(newUser.username()).id();

        return "redirect:/users/" + userId;
    }

    @PostMapping("/{id}")
    public String update(Model model, UserUpdateDto userUpdateDto, @PathVariable Long id){
        userService.update(id, userUpdateDto);

        return "redirect:/users/" + id;
    }
}
