package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ReviewRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ReviewResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponseDto<List<ReviewResponseDto>>> getReviewsByCourse(
            @PathVariable("courseId") String courseId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách đánh giá theo khóa học", reviewService.getReviewsByCourse(courseId))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> createReview(
            @AuthenticationPrincipal User user,
            @RequestBody ReviewRequestDto request
    ) {
        ReviewResponseDto created = reviewService.addReview(
                user.getId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDto.ok("Đánh giá thành công", created));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponseDto<ReviewResponseDto>> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user,
            @RequestBody ReviewRequestDto request
    ) {
        ReviewResponseDto updated = reviewService.updateReview(
                reviewId,
                user.getId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật đánh giá thành công", updated));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponseDto<String>> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        reviewService.deleteReview(reviewId, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa đánh giá thành công", null));
    }
}
