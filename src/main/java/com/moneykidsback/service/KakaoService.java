//package com.moneykidsback.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class KakaoService {
//
//    @Value("${kakao.client.id}")
//    private String kakaoClientId;
//
//    @Value("${kakao.redirect.uri}")
//    private String kakaoRedirectUri;
//
//    public String getAccessToken(String code) {
//        String url = "https://kauth.kakao.com/oauth/token";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", kakaoClientId);
//        params.add("redirect_uri", kakaoRedirectUri);
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
//
//        return (String) response.getBody().get("access_token");
//    }
//
//    public Map<String, Object> getUserInfo(String token) {
//        String url = "https://kapi.kakao.com/v2/user/me";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
//
//        Map<String, Object> body = response.getBody();
//
//        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
//        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("id", body.get("id"));
//        result.put("nickname", profile != null ? profile.get("nickname") : "익명");
//
//        return result;
//    }
//}