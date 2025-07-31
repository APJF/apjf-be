package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.CommentRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CommentResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách bình luận", commentService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết bình luận", commentService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> create(
            @Valid @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("User {} bình luận vào bài viết {}", user.getUsername(), dto.postId());
        CommentResponseDto created = commentService.create(dto);
        return ResponseEntity.created(URI.create("/api/comments/" + created.id()))
                .body(ApiResponseDto.ok("Tạo bình luận thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("User {} cập nhật bình luận {}", user.getUsername(), id);
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật bình luận thành công", commentService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        log.info("User {} xóa bình luận {}", user.getUsername(), id);
        commentService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa bình luận thành công", null));
    }
}

