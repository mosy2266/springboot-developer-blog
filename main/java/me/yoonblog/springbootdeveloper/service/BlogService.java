package me.yoonblog.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.yoonblog.springbootdeveloper.domain.Article;
import me.yoonblog.springbootdeveloper.dto.AddArticleRequest;
import me.yoonblog.springbootdeveloper.dto.UpdateArticleRequest;
import me.yoonblog.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor //final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service //해당 클래스를 서블릿 컨테이너에 빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드
    //toEntity()의 인수로 전달받은 유저 이름을 반환
    public Article save(AddArticleRequest request, String userName) {
        //save() -> JpaRepository에서 지원, AddArticleRequest 클래스에 저장된 값들을 article 데이터베이스에 저장
        return blogRepository.save(request.toEntity(userName));
    }

    //블로그 글 목록 조회 메서드
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    //블로그 글 조회 메서드
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    //블로그 글 삭제 메서드
    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    //블로그 글 수정 메서드
    @Transactional //해당 애너테이션은 매칭한 메서드를 하나의 트랜잭션으로 묶음
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    //게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
