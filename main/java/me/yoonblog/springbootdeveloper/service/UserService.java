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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) //패스워드는 암호화
                .build()).getId();
    }
}
