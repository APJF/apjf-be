package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<CourseReviewDto>>> getReviewsByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách đánh giá theo khóa học", courseReviewService.getReviewsByCourse(courseId))
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId
    ) {
        courseReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Xóa đánh giá thành công", null));
    }

    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponse<CourseReviewDto>> createReview(@RequestBody ReviewCreateRequestDto request) {
        CourseReviewDto created = courseReviewService.addReview(
                request.userId(),
                request.courseId(),
                request.rating(),
                request.comment()
        );
        return ResponseEntity.ok(ApiResponse.ok("Đánh giá thành công", created));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<CourseWithRatingDto>>> getTopRatedCourses() {
        List<CourseWithRatingDto> topCourses = courseReviewService.getTopRatedCourses(3);
        return ResponseEntity.ok(ApiResponse.ok("Top 3 khóa học được đánh giá cao nhất", topCourses));
    }


    @GetMapping("/{courseId}/avg-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable String courseId) {
        double avg = courseReviewService.getAverageRating(courseId);
        return ResponseEntity.ok(ApiResponse.ok("Điểm trung bình đánh giá", avg));
    }
}
