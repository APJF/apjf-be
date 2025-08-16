package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.PostLikeRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.PostLikeResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.PostLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post-likes")
@RequiredArgsConstructor
@Slf4j
public class PostLikeController {

    private final PostLikeService postLikeService;

    // Toggle like/unlike
    @PostMapping
    public ResponseEntity<ApiResponseDto<PostLikeResponseDto>> toggleLike(
            @Valid @RequestBody PostLikeRequestDto dto,
            @AuthenticationPrincipal User user) {

        PostLikeResponseDto response = postLikeService.toggleLike(dto, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật trạng thái like thành công", response));
    }

}
