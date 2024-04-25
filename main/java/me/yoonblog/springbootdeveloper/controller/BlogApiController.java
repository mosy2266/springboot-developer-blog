package me.yoonblog.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.yoonblog.springbootdeveloper.domain.Article;
import me.yoonblog.springbootdeveloper.dto.AddArticleRequest;
import me.yoonblog.springbootdeveloper.dto.ArticleResponse;
import me.yoonblog.springbootdeveloper.dto.UpdateArticleRequest;
import me.yoonblog.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController //해당 애너테이션을 클래스에 붙이면 HTTP 응답으로 객체 데이터를 JSON 형식으로 반환
public class BlogApiController {
    private final BlogService blogService;

    //HTTP 메서드가 POST일 때 전달받은 URL과 동일한 메서드로 매핑
    @PostMapping("/api/articles")
    //@RequestBody : HTTP를 요청할 때 응답에 해당하는 값을 해당 애너테이션이 붙은 객체에 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        //요청한 자원이 성공적으로 생성되었으며(201), 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    @GetMapping("/api/articles/{id}")
    //@PathVariable : URL 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedArticle);
    }
}
