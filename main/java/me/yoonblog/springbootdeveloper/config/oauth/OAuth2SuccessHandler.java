package me.yoonblog.springbootdeveloper.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.yoonblog.springbootdeveloper.config.jwt.TokenProvider;
import me.yoonblog.springbootdeveloper.domain.RefreshToken;
import me.yoonblog.springbootdeveloper.domain.User;
import me.yoonblog.springbootdeveloper.repository.RefreshTokenRepository;
import me.yoonblog.springbootdeveloper.service.UserService;
import me.yoonblog.springbootdeveloper.util.CookieUtil;
import org.hibernate.boot.model.internal.CreateKeySecondPass;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        //리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION); //리프레쉬 토큰 생성
        saveRefreshToken(user.getId(), refreshToken); //리프레쉬 토큰을 데이터베이스에 저장
        addRefreshTokenToCookie(request, response, refreshToken); //리프레쉬 토큰을 쿠키에 저장

        //액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        //쿠키에서 리다이렉트 경로가 담긴 값을 가져와 쿼리 파라미터에 액세스 토큰을 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION); //액세스 토큰 생성
        String targetUrl = getTargetUrl(accessToken); //액세스 토큰을 패스에 추가

        //인증 관련 설정값, 쿠키 제거
        //세션과 쿠키에 저장해둔 인증 관련 임시 데이터를 제거
        clearAuthenticationAttributes(request, response);

        //리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    //생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response,
                                         String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        //기본 제공 메서드
        super.clearAuthenticationAttributes(request);
        //removeAuthorizationRequestCookies() : OAuth 인증을 위해 저장된 정보를 삭제하기 위해 추가해준 메서드
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //액세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
