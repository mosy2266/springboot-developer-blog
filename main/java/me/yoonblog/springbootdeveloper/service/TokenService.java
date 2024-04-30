package me.yoonblog.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.yoonblog.springbootdeveloper.config.jwt.TokenProvider;
import me.yoonblog.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    //전달받은 리프레시 토큰으로 토큰 유효성 검사를 진행
    //유효한 토큰이라면 리프레시 토큰으로 사용자 ID를 검색 후 토큰 제공자의 generateToken() 메서드를 호출해 새로운 액세스 토큰 생성
    public String createNewAccessToken(String refreshToken) {
        //토큰 유효성 검사에 실패한 경우 예외를 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
