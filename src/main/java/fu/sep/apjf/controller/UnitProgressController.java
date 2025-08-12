package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.UnitProgressRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.UnitProgressResponseDto;
import fu.sep.apjf.entity.UnitProgressKey;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.UnitProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/unit-progress")
@RequiredArgsConstructor
@Slf4j
public class UnitProgressController {

    private final UnitProgressService unitProgressService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UnitProgressResponseDto>>> getAllByUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponseDto.ok("Danh sách tiến trình của các unit", unitProgressService.findByUserId(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<UnitProgressResponseDto>> create(
            @Valid @RequestBody UnitProgressRequestDto dto,
            @AuthenticationPrincipal User user) {
        UnitProgressResponseDto created = unitProgressService.create(dto);
        return ResponseEntity.created(URI.create("/api/unit-progress/" + created.unitId()))
                .body(ApiResponseDto.ok("Tạo tiến trình unit thành công", created));
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<ApiResponseDto<UnitProgressResponseDto>> update(
            @PathVariable String unitId,
            @Valid @RequestBody UnitProgressRequestDto dto,
            @AuthenticationPrincipal User user) {
        UnitProgressKey key = new UnitProgressKey(unitId, user.getId());
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cập nhật tiến trình unit thành công", unitProgressService.update(key, dto)));
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<ApiResponseDto<Void>> delete(
            @PathVariable String unitId,
            @AuthenticationPrincipal User user) {
        UnitProgressKey key = new UnitProgressKey(unitId, user.getId());
        unitProgressService.delete(key);
        return ResponseEntity.ok(ApiResponseDto.ok("Xóa tiến trình unit thành công", null));
    }
}
