package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.MaterialRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.MaterialResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Slf4j
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<MaterialResponseDto>>> getAll() {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tài liệu", materialService.findAll()));
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<ApiResponseDto<List<MaterialResponseDto>>> getAllByUnitId(@PathVariable String unitId) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tài liệu theo bài học", materialService.findByUnitId(unitId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MaterialResponseDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Chi tiết tài liệu", materialService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<MaterialResponseDto>> create(
            @Valid @RequestBody MaterialRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang tạo tài liệu mới", user.getUsername());

        MaterialResponseDto created = materialService.create(dto, null, user.getId());
        return ResponseEntity.created(URI.create("/api/materials/" + created.id()))
                .body(ApiResponseDto.ok("Tạo tài liệu thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MaterialResponseDto>> update(
            @PathVariable String id,
            @Valid @RequestBody MaterialRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.info("Staff {} đang cập nhật tài liệu {}", user.getUsername(), id);

        MaterialResponseDto updated = materialService.update(id, dto, null, user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Cập nhật tài liệu thành công", updated));
    }
}
