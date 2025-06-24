package com.moneykidsback.service;

import com.moneykidsback.model.entity.User;
import com.moneykidsback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth 인증 서비스
 * Kakao, Google 소셜 로그인 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {
    
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    
    /**
     * 카카오 로그인 처리
     */
    public User processKakaoLogin(String code) {
        try {
            // 1. 인가 코드로 액세스 토큰 획득
            String accessToken = getKakaoAccessToken(code);
            
            // 2. 액세스 토큰으로 사용자 정보 획득
            Map<String, Object> userInfo = getKakaoUserInfo(accessToken);
            
            // 3. 사용자 정보로 회원가입 또는 로그인 처리
            return processOAuthUser(userInfo, "KAKAO");
            
        } catch (Exception e) {
            log.error("카카오 로그인 처리 실패", e);
            throw new RuntimeException("카카오 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 구글 로그인 처리
     */
    public User processGoogleLogin(String code) {
        try {
            // 1. 인가 코드로 액세스 토큰 획득
            String accessToken = getGoogleAccessToken(code);
            
            // 2. 액세스 토큰으로 사용자 정보 획득
            Map<String, Object> userInfo = getGoogleUserInfo(accessToken);
            
            // 3. 사용자 정보로 회원가입 또는 로그인 처리
            return processOAuthUser(userInfo, "GOOGLE");
            
        } catch (Exception e) {
            log.error("구글 로그인 처리 실패", e);
            throw new RuntimeException("구글 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 카카오 액세스 토큰 획득
     */
    private String getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        Map<String, Object> responseBody = response.getBody();
        
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new RuntimeException("카카오 액세스 토큰 획득 실패");
        }
        
        return (String) responseBody.get("access_token");
    }
    
    /**
     * 카카오 사용자 정보 획득
     */
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, request, Map.class
        );
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("카카오 사용자 정보 획득 실패");
        }
        
        // 카카오 응답 구조에서 필요한 정보 추출
        Map<String, Object> properties = (Map<String, Object>) responseBody.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", String.valueOf(responseBody.get("id")));
        userInfo.put("name", properties != null ? properties.get("nickname") : "카카오사용자");
        userInfo.put("email", kakaoAccount != null ? kakaoAccount.get("email") : null);
        
        return userInfo;
    }
    
    /**
     * 구글 액세스 토큰 획득
     */
    private String getGoogleAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("code", code);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        Map<String, Object> responseBody = response.getBody();
        
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new RuntimeException("구글 액세스 토큰 획득 실패");
        }
        
        return (String) responseBody.get("access_token");
    }
    
    /**
     * 구글 사용자 정보 획득
     */
    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, request, Map.class
        );
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("구글 사용자 정보 획득 실패");
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", responseBody.get("id"));
        userInfo.put("name", responseBody.get("name"));
        userInfo.put("email", responseBody.get("email"));
        
        return userInfo;
    }
    
    /**
     * OAuth 사용자 정보로 회원가입 또는 로그인 처리
     */
    private User processOAuthUser(Map<String, Object> userInfo, String provider) {
        String oauthId = provider + "_" + userInfo.get("id");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        
        // 기존 사용자 조회 (OAuth ID로)
        User user = userRepository.findById(oauthId).orElse(null);
        
        if (user == null && email != null) {
            // 이메일로도 조회
            user = userRepository.findByEmail(email).orElse(null);
        }
        
        if (user == null) {
            // 신규 사용자 생성
            user = new User();
            user.setId(oauthId);
            user.setName(name != null ? name : provider + " 사용자");
            user.setEmail(email);
            user.setPassword(UUID.randomUUID().toString()); // OAuth 사용자는 비밀번호 사용 안함
            user.setPoints(1000); // 초기 포인트
            user.setProvider(provider);
            
            user = userRepository.save(user);
            log.info("새로운 OAuth 사용자 생성: {}", oauthId);
        } else {
            // 기존 사용자 정보 업데이트
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
            }
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
            }
            user = userRepository.save(user);
            log.info("기존 OAuth 사용자 로그인: {}", oauthId);
        }
        
        return user;
    }
} 