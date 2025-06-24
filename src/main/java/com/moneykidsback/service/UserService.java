package com.moneykidsback.service;

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

    @Transactional
    public User registerUser(String id, String name, String password) {
        if (userRepository.existsById(id)) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        User user = User.builder()
                .id(id)
                .name(name)
                .password(password) // TODO: 비밀번호 암호화 필수!
                .points(0)
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(String id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!user.getPassword().equals(password)) { // TODO: 암호화된 비밀번호 비교 필수!
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        
        return user;
    }

    // -- 아래는 소셜 로그인과 기타 기능에 필요한 메서드 --
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User findOrCreateUser(String id, String nickname, String provider) {
        return userRepository.findById(id).orElseGet(() -> {
            User newUser = User.builder()
                    .id(id)
                    .name(nickname)
                    .password(null)
                    .tendency(provider)
                    .points(0)
                    .build();
            return userRepository.save(newUser);
        });
    }
}