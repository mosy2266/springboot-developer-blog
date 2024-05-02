package me.yoonblog.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor
@Getter
@Entity
//UserDetails를 상속받아 인증 객체로 사용함
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    //사용자 이름(nickname)을 추가했으므로
    @Column(name = "nickname", unique = true)
    private String nickname;

    //생성자에도 nickname을 추가
    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    //UserDetails 클래스(스프링 시큐리티에서 사용자의 인증 정보를 담아두는 인터페이스)에서 제공하는 필수 오버라이드 메서드
    @Override //권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override //사용자 id 반환(고유값)
    public String getUsername() {
        return email;
    }

    @Override //사용자 패스워드 반환
    public String getPassword() {
        return password;
    }

    @Override //계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return true; //만료되었는지 확인 -> true : 만료되지 않았음
    }

    @Override //계정 잠금 여부 반환
    public boolean isAccountNonLocked() {
        return true; //계정이 잠금되었는지 확인 -> true : 잠기지 않았음
    }

    @Override //패스워드 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        return true; //패스워드가 만료되었는지 확인 -> true : 만료되지 않았음
    }

    @Override //계정 사용 가능 여부 반환
    public boolean isEnabled() {
        return true; //계정이 사용 가능한지 확인 -> true : 사용 가능
    }

    //사용자 이름 변경
    public User update(String nickname) {
        this.nickname = nickname;

        return this;
    }
}
