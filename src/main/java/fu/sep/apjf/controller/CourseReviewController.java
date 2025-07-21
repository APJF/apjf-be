package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.CourseReviewDto;
import fu.sep.apjf.dto.CourseWithRatingDto;
import fu.sep.apjf.dto.ReviewCreateRequestDto;
import fu.sep.apjf.service.CourseReviewService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;
    /*
    @PostMapping("/review")
    public ResponseEntity<ApiResponse<CourseReviewDto>> addReview(
            @RequestParam Long userId,
            @RequestParam String courseId,
            @RequestParam @Min(1) @Max(5) int rating,
            @RequestParam(required = false) String comment
    ) {
        CourseReviewDto created = courseReviewService.addReview(userId, courseId, rating, comment);
        return ResponseEntity.ok(ApiResponse.ok("Đánh giá thành công", created));
    }
    */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<CourseReviewDto>>> getReviewsByCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách đánh giá theo khóa học", courseReviewService.getReviewsByCourse(courseId))
        );
    }
    /*
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CourseReviewDto>>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách đánh giá theo người dùng", courseReviewService.getReviewsByUser(userId))
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId
    ) {
        courseReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Xóa đánh giá thành công", null));
    }
    */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CourseReviewDto>> createReview(@RequestBody ReviewCreateRequestDto request) {
        CourseReviewDto created = courseReviewService.addReview(
                request.getUserId(),
                request.getCourseId(),
                request.getRating(),
                request.getComment()
        );
        return ResponseEntity.ok(ApiResponse.ok("Đánh giá thành công", created));
    }

    @GetMapping("/top-courses")
    public ResponseEntity<ApiResponse<List<CourseWithRatingDto>>> getTopRatedCourses() {
        List<CourseWithRatingDto> topCourses = courseReviewService.getTopRatedCourses(3);
        return ResponseEntity.ok(ApiResponse.ok("Top 3 khóa học được đánh giá cao nhất", topCourses));
    }


    @GetMapping("/course/{courseId}/avg-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable String courseId) {
        double avg = courseReviewService.getAverageRating(courseId);
        return ResponseEntity.ok(ApiResponse.ok("Điểm trung bình đánh giá", avg));
    }
}
