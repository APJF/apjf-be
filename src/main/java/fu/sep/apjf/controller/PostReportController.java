package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.PostReportRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.PostReportResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.PostReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/post-reports")
@RequiredArgsConstructor
@Slf4j
public class PostReportController {

    private final PostReportService postReportService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<PostReportResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách báo cáo bài viết", postReportService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostReportResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết báo cáo", postReportService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<PostReportResponseDto>> create(
            @Valid @RequestBody PostReportRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("User {} gửi báo cáo bài viết: {}", user.getUsername(), dto.postId());
        PostReportResponseDto created = postReportService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/post-reports/" + created.id()))
                .body(ApiResponseDto.ok("Gửi báo cáo thành công", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        log.info("User {} xóa báo cáo {}", user.getUsername(), id);
        postReportService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa báo cáo thành công", null));
    }
}

