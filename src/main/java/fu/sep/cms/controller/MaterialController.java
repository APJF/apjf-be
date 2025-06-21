package fu.sep.cms.controller;

import fu.sep.cms.entity.Material;
import fu.sep.cms.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    // Create
    @PostMapping
    public ResponseEntity<Material> create(@RequestBody Material material) {
        return ResponseEntity.ok(materialService.create(material));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Material> update(@PathVariable Long id, @RequestBody Material material) {
        return ResponseEntity.ok(materialService.update(id, material));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // View by Slot ID
    @GetMapping("/slot/{slotId}")
    public ResponseEntity<List<Material>> getBySlotId(@PathVariable Long slotId) {
        return ResponseEntity.ok(materialService.getBySlotId(slotId));
    }

    // View by Material ID
    @GetMapping("/{id}")
    public ResponseEntity<Material> getById(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.getById(id));
    }
}
