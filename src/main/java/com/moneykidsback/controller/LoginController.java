package com.moneykidsback.controller;

import com.moneykidsback.model.User;
import com.moneykidsback.service.GoogleService;
import com.moneykidsback.service.KakaoService;
import com.moneykidsback.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/users/login")
public class LoginController {

    private final KakaoService kakaoService;
    private final UserService userService;
    private final GoogleService googleService;

    @Value("${kakao.client.id}")
    private String KakaoClientId;
    @Value("${kakao.redirect.uri}")
    private String KakaoRedirectUri;

    @Value("${google.client.id}")
    private String GoogleClientId;
    @Value("${google.redirect.uri}")
    private String GoogleRedirectUri;

    public LoginController(KakaoService kakaoService, UserService userService, GoogleService googleService) {
        this.kakaoService = kakaoService;
        this.userService = userService;
        this.googleService = googleService;
    }

    @PostMapping
    public String login(@RequestParam String id,
                        @RequestParam String password,
                        Model model) {

        Optional<User> optionalUser = userService.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                return "redirect:/home";
            }
        }

        model.addAttribute("errorMessage", "아이디 또는 비밀번호가 틀렸습니다.");
        return "LoginPage"; // templates/LoginPage.html
    }


    // 카카오톡
    @GetMapping("/kakao")
    public String kakaoRedirect() {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + KakaoClientId
                + "&redirect_uri=" + KakaoRedirectUri
                + "&response_type=code"
                + "&prompt=login";
        return "redirect:" + url;
    }
    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam String code) {
        String token = kakaoService.getAccessToken(code);
        Map<String, Object> userInfo = kakaoService.getUserInfo(token);

        String kakaoId = userInfo.get("id").toString();
        String nickname = userInfo.get("nickname").toString();

        // 자동 가입 처리
        userService.findOrCreateUser(kakaoId, nickname, "kakao");

        return "redirect:/home";
    }

    // 구글
    @GetMapping("/google")
    public String googleRedirect() {
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + GoogleClientId
                + "&redirect_uri=" + GoogleRedirectUri
                + "&response_type=code"
                + "&scope=openid%20profile%20email"
                + "&prompt=consent";
        return "redirect:" + url;
    }
    @GetMapping("/google/callback")
    public String googleCallback(@RequestParam String code) {
        String token = googleService.getAccessToken(code);
        Map<String, Object> userInfo = googleService.getUserInfo(token);

        String googleId = (String) userInfo.get("id");
        String name = (String) userInfo.get("name");

        userService.findOrCreateUser(googleId, name, "google");
        return "redirect:/home";
    }

}
