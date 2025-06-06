package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.UserRegisterRequest;
import com.moneykidsback.model.User;
import com.moneykidsback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegisterRequest request, Model model) {
        if (userRepository.existsById(request.getId())) {
            model.addAttribute("errorMessage", "이미 존재하는 ID입니다.");
            return "RegisterPage";
        }

        User user = User.builder()
                .id(request.getId())
                .name(request.getName())
                .password(request.getPassword())
                .points(0)
                .build();

        userRepository.save(user);

        return "redirect:/api/users/login";
    }
}
