package com.moneykidsback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "LoginPage"; // templates/LoginPage.html 파일을 보여줌
    }

    @GetMapping("/register")
    public String registerPage() {
        return "RegisterPage"; // templates/RegisterPage.html 파일을 보여줌
    }

    @GetMapping("/home")
    public String homePage() {
        return "HomePage"; // templates/HomePage.html 파일을 보여줌
    }
}