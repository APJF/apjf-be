package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.UnitRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.UnitResponseDto;
import fu.sep.apjf.entity.User;
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

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UnitResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách bài học", unitService.list()));
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ApiResponseDto<List<UnitResponseDto>>> getAllByChapterId(@PathVariable String chapterId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách bài học theo chương", unitService.findByChapterId(chapterId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UnitResponseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết bài học", unitService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<UnitResponseDto>> create(
            @Valid @RequestBody UnitRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang tạo bài học mới: {}", user.getUsername(), dto.id());

        UnitResponseDto unitDto = unitService.create(dto, user.getId());
        return ResponseEntity.created(URI.create("/api/units/" + unitDto.id()))
                .body(ApiResponseDto.ok("Tạo bài học thành công", unitDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UnitResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody UnitRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang cập nhật bài học: {}", user.getUsername(), id);

        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật bài học thành công", unitService.update(id, dto, user.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang xóa bài học {}", user.getUsername(), id);

        unitService.delete(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa bài học thành công", null));
    }
}