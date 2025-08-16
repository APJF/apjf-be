package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ExamListResponseDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ExamService;
import fu.sep.apjf.service.MaterialService;
import fu.sep.apjf.service.ProgressTrackingService;
import fu.sep.apjf.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Slf4j
public class UnitController {

    private final UnitService unitService;
    private final MaterialService materialService;
    private final ExamService examService;
    private final ProgressTrackingService trackingService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UnitResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách bài học", unitService.list()));
    }

    @GetMapping("/{unitId}/materials")
    public ResponseEntity<ApiResponseDto<List<MaterialResponseDto>>> getMaterialsByUnitId(@PathVariable String unitId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tài liệu của bài học", materialService.findByUnitId(unitId)));
    }

    @GetMapping("/{unitId}/exams")
    public ResponseEntity<ApiResponseDto<List<ExamListResponseDto>>> getExamsByUnitId(@PathVariable String unitId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách exams của bài học", examService.findByUnitId(unitId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UnitResponseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết bài học", unitService.findById(id)));
    }

    @PostMapping("/{unitId}/pass")
    public ResponseEntity<ApiResponseDto<Void>> markUnitPassed(
            @PathVariable String unitId,
            @AuthenticationPrincipal User user) {
        trackingService.markUnitPassed(unitId, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Đánh dấu hoàn thành bài học thành công", null));
    }


    @PostMapping
    public ResponseEntity<ApiResponseDto<UnitResponseDto>> create(
            @Valid @RequestBody UnitRequestDto dto,
            @AuthenticationPrincipal User user) {
        UnitResponseDto unitDto = unitService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/units/" + unitDto.id()))
                .body(ApiResponseDto.ok("Tạo bài học thành công", unitDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UnitResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody UnitRequestDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật bài học thành công", unitService.update(id, dto, user.getId())));
    }

}