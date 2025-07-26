package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.CourseReviewDto;
import fu.sep.apjf.dto.CourseWithRatingDto;
import fu.sep.apjf.dto.ReviewCreateRequestDto;
import fu.sep.apjf.service.CourseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseDto<List<CourseReviewDto>>> getReviewsByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách đánh giá theo khóa học", courseReviewService.getReviewsByCourse(courseId))
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseDto<String>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId
    ) {
        courseReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa đánh giá thành công", null));
    }

    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseDto<CourseReviewDto>> createReview(@RequestBody ReviewCreateRequestDto request) {
        CourseReviewDto created = courseReviewService.addReview(
                request.userId(),
                request.courseId(),
                request.rating(),
                request.comment()
        );
        return ResponseEntity.ok(ApiResponseDto.ok("Đánh giá thành công", created));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponseDto<List<CourseWithRatingDto>>> getTopRatedCourses() {
        List<CourseWithRatingDto> topCourses = courseReviewService.getTopRatedCourses(3);
        return ResponseEntity.ok(ApiResponseDto.ok("Top 3 khóa học được đánh giá cao nhất", topCourses));
    }


    @GetMapping("/{courseId}/avg-rating")
    public ResponseEntity<ApiResponseDto<Double>> getAverageRating(@PathVariable String courseId) {
        double avg = courseReviewService.getAverageRating(courseId);
        return ResponseEntity.ok(ApiResponseDto.ok("Điểm trung bình đánh giá", avg));
    }
}
