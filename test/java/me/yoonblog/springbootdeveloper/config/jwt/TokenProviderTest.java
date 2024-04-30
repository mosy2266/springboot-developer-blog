package me.yoonblog.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwts;
import me.yoonblog.springbootdeveloper.domain.User;
import me.yoonblog.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    //generateToken() 검증 테스트
    //토큰 생성 메서드를 테스트
    @DisplayName("generateToken() 테스트 : 유저 정보와 만료 기간을 전달해 토큰 생성")
    @Test
    void generateToken() {
        //given : 유저 정보를 추가하기 위한 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        //when : 토큰 제공자의 generateToken() 메서드를 호출해 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //then : jjwt 라이브러리를 사용해 토큰 복호화 -> 토큰 생성할 때 클레임으로 넣어둔 id값이 given의 유저 ID와 동일한지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    //validToken() 검증 테스트
    @DisplayName("validToken() 테스트 : 만료된 토큰일 때 유효성 검증 실패")
    @Test
    void validToken_invalidToken() {
        //given : jjwt 라이브러리를 사용해 토큰 생성
        String token = JwtFactory.builder()
                //1970.01.01부터 현재까지의 시간을 밀리초로 치환한 값(new Date().getTime())에 1000을 빼 이미 만료된 토큰으로 생성
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when : 토큰 제공자의 validToken() 메서드를 호출, 유효한 토큰인지 검증한 뒤 결과를 반환받음
        boolean result = tokenProvider.validToken(token);

        //then : 반환값이 false(유효한 토큰이 아님)임을 확인
        assertThat(result).isFalse();
    }

    @DisplayName("validToken() 테스트 : 유효한 토큰일 때 유효성 검증 성공")
    @Test
    void validToken_validToken() {
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isTrue();
    }

    //getAuthentication() 검증 테스트
    @DisplayName("getAuthentication() 테스트 : 토큰 기반으로 인증 정보를 가져옴")
    @Test
    void getAuthentication() {
        //given : 토큰 생성 -> 이때 토큰 제목 subject는 "user@email.com"
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        //when : 토큰 제공자의 getAuthentication() 메서드 호출 -> 인증 객체 반환받음
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then : 반환받은 인증 객체의 유저 이름이 given의 subject값인 "user@email.com"과 같은지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    //getUserId() 검증 테스트
    @DisplayName("getUserId() 테스트 : 토큰으로 유저 ID를 가져옴")
    @Test
    void getUserId() {
        //given : 토큰 생성 -> 이때 클레임 추가, 키는 "id", 값은 1
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        //when : 토큰 제공자의 getUserId() 메서드 호출 -> 유저 ID 반환받음
        Long userIdByToken = tokenProvider.getUserId(token);

        //then : 반환받은 유저 ID가 given의 유저 ID값인 1과 같은지 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
