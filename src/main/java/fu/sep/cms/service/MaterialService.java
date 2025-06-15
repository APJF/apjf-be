package fu.sep.cms.service;

import fu.sep.cms.entity.Material;
import org.springframework.data.domain.Page;

public interface MaterialService {
    Material createMaterial(Material material);
    Material updateMaterial(Long id, Material material);
    void deleteMaterial(Long id);
    Page<Material> getAllMaterials(int page, int size);
    Page<Material> searchMaterials(String keyword, int page, int size);
}
