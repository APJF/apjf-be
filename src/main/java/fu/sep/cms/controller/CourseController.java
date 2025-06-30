package fu.sep.cms.controller;

import fu.sep.cms.dto.ApiResponse;
import fu.sep.cms.dto.CourseDetailDto;
import fu.sep.cms.dto.CourseDto;
import fu.sep.cms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /* -------- GET /api/courses -------- */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.ok("List courses", courseService.findAll()));
    }

    /* -------- GET /api/courses/{id} -------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Course detail", courseService.findById(id)));
    }

    /* -------- GET /api/courses/{id}/detail -------- */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<CourseDetailDto>> getFullDetail(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Course + chapters + units", courseService.findDetail(id)));
    }

    /* -------- POST /api/courses -------- */
    @PostMapping
    public ResponseEntity<ApiResponse<CourseDto>> create(@RequestBody CourseDto dto) {
        CourseDto created = courseService.create(dto);
        return ResponseEntity.created(URI.create("/api/courses/" + created.id()))
                .body(ApiResponse.ok("Course created", created));
    }

    /* -------- PUT /api/courses/{id} -------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDto>> update(@PathVariable String id,
                                                         @RequestBody CourseDto dto) {
        return ResponseEntity.ok(
                ApiResponse.ok("Course updated", courseService.update(id, dto)));
    }
}