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

    @GetMapping("/list")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách bài viết", postService.list(user.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết bài viết", postService.get(id, user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<PostResponseDto>> create(
            @Valid @RequestBody PostRequestDto dto,
            @AuthenticationPrincipal User user) {

        PostResponseDto created = postService.create(dto,user.getId());
        return ResponseEntity.created(URI.create("/api/posts/" + created.id()))
                .body(ApiResponseDto.ok("Tạo bài viết thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDto dto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật bài viết thành công", postService.update(id, dto, user.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa bài viết thành công", null));
    }
}
