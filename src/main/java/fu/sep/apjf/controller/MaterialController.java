package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.MaterialDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MaterialDto>>> getAllMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu",
                        materialService.findAll(page, size, sortBy, direction)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDto>> getMaterialById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Chi tiết tài liệu",
                        materialService.findById(id)));
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<ApiResponse<List<MaterialDto>>> getMaterialsByUnit(@PathVariable String unitId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu theo bài học",
                        materialService.findByUnit(unitId)));
    }

    @GetMapping("/unit/{unitId}/paginated")
    public ResponseEntity<ApiResponse<Page<MaterialDto>>> getMaterialsByUnitPaginated(
            @PathVariable String unitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu theo bài học (phân trang)",
                        materialService.findByUnit(unitId, page, size)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<MaterialDto>>> getMaterialsByType(
            @PathVariable EnumClass.MaterialType type) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu theo loại",
                        materialService.findByType(type)));
    }

    @GetMapping("/type/{type}/paginated")
    public ResponseEntity<ApiResponse<Page<MaterialDto>>> getMaterialsByTypePaginated(
            @PathVariable EnumClass.MaterialType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu theo loại (phân trang)",
                        materialService.findByType(type, page, size)));
    }

    @GetMapping("/unit/{unitId}/type/{type}")
    public ResponseEntity<ApiResponse<List<MaterialDto>>> getMaterialsByUnitAndType(
            @PathVariable String unitId,
            @PathVariable EnumClass.MaterialType type) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu theo bài học và loại",
                        materialService.findByUnitAndType(unitId, type)));
    }

    @GetMapping("/unit/{unitId}/type/{type}/paginated")
    public ResponseEntity<ApiResponse<Page<MaterialDto>>> getMaterialsByUnitAndTypePaginated(
            @PathVariable String unitId,
            @PathVariable EnumClass.MaterialType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tài liệu theo bài học và loại (phân trang)",
                        materialService.findByUnitAndType(unitId, type, page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MaterialDto>>> searchMaterials(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                ApiResponse.ok("Kết quả tìm kiếm tài liệu",
                        materialService.searchByDescription(keyword)));
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<ApiResponse<Page<MaterialDto>>> searchMaterialsPaginated(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok("Kết quả tìm kiếm tài liệu (phân trang)",
                        materialService.searchByDescription(keyword, page, size)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialDto>> createMaterial(
            @Valid @RequestBody MaterialDto materialDto,
            @RequestHeader("X-User-Id") String staffId) {
        MaterialDto createdMaterial = materialService.create(materialDto, staffId);
        return ResponseEntity.created(URI.create("/api/materials/" + createdMaterial.id()))
                .body(ApiResponse.ok("Tạo tài liệu thành công", createdMaterial));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDto>> updateMaterial(
            @PathVariable String id,
            @Valid @RequestBody MaterialDto materialDto,
            @RequestHeader("X-User-Id") String staffId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Cập nhật tài liệu thành công",
                        materialService.update(id, materialDto, staffId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String staffId) {
        materialService.delete(id, staffId);
        return ResponseEntity.ok(
                ApiResponse.ok("Đã tạo yêu cầu xóa tài liệu thành công", null));
    }
}
