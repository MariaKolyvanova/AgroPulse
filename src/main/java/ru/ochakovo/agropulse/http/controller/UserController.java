package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ochakovo.agropulse.dto.user.UserCreateDto;
import ru.ochakovo.agropulse.dto.user.UserReadDto;
import ru.ochakovo.agropulse.dto.user.UserUpdateDto;
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


        return "users/user";
    }

    @GetMapping
    public String users(Model model, Authentication auth) {
        UserReadDto currentUser = userService.findByUsername(auth.getName()).orElseThrow();

        model.addAttribute("users", userService.findAll(currentUser.companyId()));

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
                         RedirectAttributes redirectAttributes, Authentication auth) {

        if (bindingResultUser.hasErrors()) {
            redirectAttributes.addFlashAttribute("newUser", newUser);
            redirectAttributes.addFlashAttribute("userErrors", bindingResultUser.getAllErrors());
            return "redirect:/users/create";
        }

        Long companyId = userService.findByUsername(auth.getName()).get().companyId();
        userService.create(newUser, companyId);
        Long userId = userService.findByUsername(newUser.username()).get().id();

        return "redirect:/users/" + userId;
    }

    @PostMapping("/{id}")
    public String update(Model model, UserUpdateDto userUpdateDto, @PathVariable Long id){
        userService.update(id, userUpdateDto);

        return "redirect:/users/" + id;
    }
}
