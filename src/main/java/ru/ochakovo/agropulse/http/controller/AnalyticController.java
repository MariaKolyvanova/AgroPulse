package ru.ochakovo.agropulse.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/analytic")
@Controller
@RequiredArgsConstructor
public class AnalyticController {

    @GetMapping("calculation")
    public String calculation() {

        return "analytic/calculation";
    }

    @GetMapping("/analytic")
    public String analytic() {

        return "analytic/analytic";
    }

    @GetMapping("/archive")
    public String archive() {

        return "analytic/archive";
    }

    @GetMapping("/export")
    public String export() {

        return "analytic/export";
    }
}

