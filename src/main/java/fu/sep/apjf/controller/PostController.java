package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.PostRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.PostResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách bài viết", postService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết bài viết", postService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<PostResponseDto>> create(
            @Valid @RequestBody PostRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("User {} tạo bài viết: {}", user.getUsername(), dto.title());
        PostResponseDto created = postService.create(dto);
        return ResponseEntity.created(URI.create("/api/posts/" + created.id()))
                .body(ApiResponseDto.ok("Tạo bài viết thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("User {} cập nhật bài viết {}", user.getUsername(), id);
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật bài viết thành công", postService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        log.info("User {} xóa bài viết {}", user.getUsername(), id);
        postService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa bài viết thành công", null));
    }
}
