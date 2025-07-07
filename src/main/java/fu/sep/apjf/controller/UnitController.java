package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.UnitDto;
import fu.sep.apjf.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnitDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách bài học", unitService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Chi tiết bài học", unitService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UnitDto>> create(@Valid @RequestBody UnitDto dto,
                                                       @RequestHeader("X-User-Id") String staffId) {
        UnitDto created = unitService.create(dto, staffId);
        return ResponseEntity.created(URI.create("/api/units/" + created.id()))
                .body(ApiResponse.ok("Tạo bài học thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitDto>> update(@PathVariable String id,
                                                       @Valid @RequestBody UnitDto dto,
                                                       @RequestHeader("X-User-Id") String staffId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Cập nhật bài học thành công", unitService.update(id, dto, staffId)));
    }
}