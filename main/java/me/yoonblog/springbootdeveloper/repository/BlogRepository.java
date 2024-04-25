package me.yoonblog.springbootdeveloper.repository;

import me.yoonblog.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

//BlogRepository 인터페이스가 JpaRepository 클래스를 상속받음
//이때 엔티티 Aritcle과 엔티티의 PK 타입 Long을 인수로 넣음
public interface BlogRepository extends JpaRepository<Article, Long> {
}
