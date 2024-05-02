package me.yoonblog.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity //엔티티로 지정
@Getter //getId(), getTitle() 등 get 관련 메서드가 해당 애너테이션으로 대체 -> 접근자 메서드 역할
//proteced Article() {} 코드가 해당 애너테이션으로 대체 -> 기본 생성자 역할
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id //id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키를 자동으로 1씩 증가
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false) //'title'이라는 not null column과 매핑
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    //글쓴이 추가
    @Column(name = "author", nullable = false)
    private String author;

    @Builder //빌더 패턴으로 객체를 생성
    public Article(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    //글 수정을 위한 메서드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @CreatedDate //엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate //엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
