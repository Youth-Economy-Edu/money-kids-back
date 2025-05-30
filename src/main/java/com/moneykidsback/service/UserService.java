package com.moneykidsback.service;

import com.moneykidsback.model.User;
import com.moneykidsback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User findOrCreateUser(String id, String nickname, String provider) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            User user = User.builder()
                    .id(id)
                    .name(nickname)
                    .password(provider)
                    .points(0)
                    .build();
            userRepository.save(user);
            return user;
        } else {
            return optionalUser.get();
        }
    }
}