package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.CommentReportRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CommentReportResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.CommentReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comment-reports")
@RequiredArgsConstructor
@Slf4j
public class CommentReportController {

    private final CommentReportService commentReportService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CommentReportResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách báo cáo bình luận", commentReportService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CommentReportResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết báo cáo bình luận", commentReportService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CommentReportResponseDto>> create(
            @Valid @RequestBody CommentReportRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("User {} báo cáo bình luận {}", user.getUsername(), dto.commentId());
        CommentReportResponseDto created = commentReportService.create(dto);
        return ResponseEntity.created(URI.create("/api/comment-reports/" + created.id()))
                .body(ApiResponseDto.ok("Gửi báo cáo thành công", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        log.info("User {} xóa báo cáo {}", user.getUsername(), id);
        commentReportService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa báo cáo thành công", null));
    }
}

