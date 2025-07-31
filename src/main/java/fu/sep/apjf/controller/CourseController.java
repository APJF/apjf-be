package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.CourseRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.CourseResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.CourseService;
import fu.sep.apjf.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<CourseResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách chương", courseService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết khóa học", courseService.findById(id)));
    }


    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponseDto<List<CourseResponseDto>>> getTopRatedCourses() {
        List<CourseResponseDto> topCourses = reviewService.getTopRatedCourses(3);
        return ResponseEntity.ok(ApiResponseDto.ok("Top 3 khóa học được đánh giá cao nhất", topCourses));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> create(
            @Valid @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User user) {
        CourseResponseDto created = courseService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/courses/" + created.id()))
                .body(ApiResponseDto.ok("Tạo khóa học thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CourseResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody CourseRequestDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật khóa học thành công", courseService.update(id, dto, user.getId())));
    }

}