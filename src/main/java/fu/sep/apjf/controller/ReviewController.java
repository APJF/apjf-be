package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ReviewRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseDto<List<CourseResponseDto>>> getReviewsByCourse(@PathVariable("courseId") String courseId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách đánh giá theo khóa học", reviewService.getReviewsByCourse(courseId))
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseDto<String>> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam Long userId
    ) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa đánh giá thành công", null));
    }

    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> createReview(
            @PathVariable("courseId") String courseId,
            @RequestBody ReviewRequestDto request) {
        CourseResponseDto created = reviewService.addReview(
                request.userId(),
                courseId,
                request.rating(),
                request.comment()
        );
        return ResponseEntity.ok(ApiResponseDto.ok("Đánh giá thành công", created));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponseDto<List<CourseResponseDto>>> getTopRatedCourses() {
        List<CourseResponseDto> topCourses = reviewService.getTopRatedCourses(3);
        return ResponseEntity.ok(ApiResponseDto.ok("Top 3 khóa học được đánh giá cao nhất", topCourses));
    }

    @GetMapping("/{courseId}/avg-rating")
    public ResponseEntity<ApiResponseDto<Double>> getAverageRating(@PathVariable("courseId") String courseId) {
        double avg = reviewService.getAverageRating(courseId);
        return ResponseEntity.ok(ApiResponseDto.ok("Điểm trung bình đánh giá", avg));
    }
}
