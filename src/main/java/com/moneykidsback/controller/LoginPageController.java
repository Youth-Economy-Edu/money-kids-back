package com.moneykidsback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
public class LoginPageController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "LoginPage";
    }
}