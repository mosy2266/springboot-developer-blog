/*package me.yoonblog.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.yoonblog.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    //스프링 시큐리티 기능 비활성화 -> 인증, 인가 서비스를 모든 곳에는 적용하지 않음
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) //특정 요청과 일치하는 url에 대한 액세스를 설정
                .requestMatchers("/static/**");
    }

    //특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests() //인증, 인가 설정
                //.permitAll() : 누구나 접근 가능하게 설정
                .requestMatchers("/login", "/signup", "/user").permitAll()
                //.anyRequest() : 위에서 설정한 url 이외의 요청에 대해서 설정
                //.authenticatied() : 별도의 인가는 필요하지 않지만 인증이 성공된 상태여야 접근 가능하게 설정
                .anyRequest().authenticated()
                .and()
                .formLogin() //폼 기반 로그인 설정
                .loginPage("/login") //로그인 페이지 경로 설정
                .defaultSuccessUrl("/articles") //로그인이 완료되었을 때 이동할 경로 설정
                .and()
                .logout() //로그아웃 설정
                .logoutSuccessUrl("/login") //로그아웃이 완료되었을 때 이동할 경로 설정
                .invalidateHttpSession(true) //로그아웃 이후에 세션을 전체 삭제할지 여부를 설정
                .and()
                .csrf().disable() //csrf 비활성화
                .build();
    }

    //인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                //사용자 정보 서비스 설정
                //.userDetailsService() : 사용자 정보를 가져올 서비스를 설정 -> 이때 설정하는 서비스 클래스는
                //반드시 UserDetailsService를 상속받은 클래스여야 함
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder) //비밀번호를 암호화하기 위한 인코더 설정
                .and()
                .build();
    }

    //패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
*/