package fu.sep.cms.controller;

import fu.sep.cms.dto.ApiResponse;
import fu.sep.cms.dto.UnitDto;
import fu.sep.cms.service.UnitService;
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
                ApiResponse.ok("List units", unitService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Unit", unitService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UnitDto>> create(@RequestBody UnitDto dto) {
        UnitDto created = unitService.create(dto);
        return ResponseEntity.created(URI.create("/api/units/" + created.id()))
                .body(ApiResponse.ok("Unit created", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitDto>> update(@PathVariable String id,
                                                       @RequestBody UnitDto dto) {
        return ResponseEntity.ok(
                ApiResponse.ok("Unit updated", unitService.update(id, dto)));
    }
}