package ru.ochakovo.agropulse.http.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.ochakovo.agropulse.dto.UserReadDto;


@RequestMapping("/api")
@Controller
@SessionAttributes({"user"})
public class GreetingController {

    @GetMapping("/hello")
    public String hello(Model model, HttpServletRequest request, UserReadDto userReadDto) {

        model.addAttribute("user", userReadDto);


        return "greeting/hello";
    }


    @GetMapping("/bye")
    public String bye(Model model, @SessionAttribute("user") UserReadDto userReadDto) {

        return "greeting/bye";
    }


    //    Пример как пользоваться параметрами через @ + куки
    //    @GetMapping("hello/{id}")
    //    public String hello2(ModelAndView modelAndView,
    //                               HttpServletRequest request,
    //                               @RequestParam Integer age,
    //                               @RequestHeader String accept,
    //                               @CookieValue("JSESSIONID") String jSessionId,
    //                               @PathVariable Integer id){
    //
    //        String ageParam = request.getParameter("age");
    //        String acceptHeader = request.getHeader("accept");
    //        Cookie[] cookies = request.getCookies();
    //
    //        modelAndView.setViewName("greeting/hello");
    //
    //        return "greeting/hello";
    //    }

}
