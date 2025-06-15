package fu.sep.cms.controller;

import fu.sep.cms.entity.Material;
import fu.sep.cms.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public Material create(@RequestBody Material material) {
        return materialService.createMaterial(material);
    }

    @PutMapping("/{id}")
    public Material update(@PathVariable Long id, @RequestBody Material material) {
        return materialService.updateMaterial(id, material);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        materialService.deleteMaterial(id);
    }

    @GetMapping
    public Page<Material> getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return materialService.getAllMaterials(page, size);
    }

    @GetMapping("/search")
    public Page<Material> search(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return materialService.searchMaterials(keyword, page, size);
    }
}
