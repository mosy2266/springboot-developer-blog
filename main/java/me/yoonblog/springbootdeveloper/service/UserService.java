package me.yoonblog.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.yoonblog.springbootdeveloper.dto.AddUserRequest;
import me.yoonblog.springbootdeveloper.domain.User;
import me.yoonblog.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword())) //패스워드는 암호화
                .build()).getId();
    }

    //전달받은 유저 ID로 유저를 검색해서 전달
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
