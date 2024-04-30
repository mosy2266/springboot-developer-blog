package me.yoonblog.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yoonblog.springbootdeveloper.config.jwt.JwtFactory;
import me.yoonblog.springbootdeveloper.config.jwt.JwtProperties;
import me.yoonblog.springbootdeveloper.domain.RefreshToken;
import me.yoonblog.springbootdeveloper.domain.User;
import me.yoonblog.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.yoonblog.springbootdeveloper.repository.RefreshTokenRepository;
import me.yoonblog.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken : 새로운 액세스 토큰 발급")
    @Test
    public void createNewAccessToken() throws Exception {
        //given : 테스트 유저를 생성, jjwt 라이브러리를 이용해 리프레시 토큰을 만들어 데이터베이스에 저장
        //토큰 생성 API의 요청 본문에 리프레시 토큰을 포함하여 요청 객체 생성
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        //when : 토큰 추가 API에 요청을 보냄 -> 요청 타입은 JSON, given에서 만들어 둔 객체를 요청 본문에 함께 보냄
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then : 응답 코드가 201 Created인지 확인하고 응답으로 온 액세스 토큰이 비어있지 않은지 확인
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}