//package com.moneykidsback.config.security;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import java.util.Collection;
//import org.springframework.security.core.GrantedAuthority;
//
//public class CustomUserDetails implements UserDetails {
//    private final String username; // user_id로 사용
//
//    public CustomUserDetails(String username) {
//        this.username = username;
//    }
//
//    @Override
//    public String getUsername() { return username; }
//    @Override
//    public String getPassword() { return null; }
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
//    @Override
//    public boolean isAccountNonExpired() { return true; }
//    @Override
//    public boolean isAccountNonLocked() { return true; }
//    @Override
//    public boolean isCredentialsNonExpired() { return true; }
//    @Override
//    public boolean isEnabled() { return true; }
//}