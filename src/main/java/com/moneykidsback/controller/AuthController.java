package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.UserRegisterRequest;
import com.moneykidsback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegisterRequest request, Model model) {
        try {
            userService.registerUser(request);
            // 회원가입 성공 시 로그인 페이지로 이동
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            // ID 중복 등 예외 발생 시 에러 메시지와 함께 다시 회원가입 페이지를 보여줌
            model.addAttribute("errorMessage", e.getMessage());
            return "RegisterPage";
        }
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserRegisterRequest request, Model model) {
        try {
            userService.login(request);
            // 로그인 성공 시 홈 페이지로 이동
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 에러 메시지와 함께 다시 로그인 페이지를 보여줌
            model.addAttribute("errorMessage", e.getMessage());
            return "LoginPage";
        }
    }
}