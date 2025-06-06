package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.UserRegisterRequest; // LoginRequestDto 대신 사용
import com.moneykidsback.model.entity.User;
import com.moneykidsback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    // ... (findOrCreateUser, findById 메서드는 그대로 유지) ...

    @Transactional
    public void registerUser(UserRegisterRequest request) {
        if (userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        User user = User.builder()
                .id(request.getId())
                .name(request.getName())
                .password(request.getPassword())
                .points(0)
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void login(UserRegisterRequest request) { // LoginRequestDto 대신 사용
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
    }
    
    // findOrCreateUser, findById 메서드도 여기에 포함되어야 합니다.
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User findOrCreateUser(String id, String nickname, String provider) {
        return userRepository.findById(id).orElseGet(() -> {
            User newUser = User.builder()
                    .id(id).name(nickname).password(null).tendency(provider).points(0)
                    .build();
            return userRepository.save(newUser);
        });
    }
}